/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.opentripplanner.standalone;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class OTPConfigurator {
    
    private static final Logger LOG = LoggerFactory.getLogger(OTPConfigurator.class);

    private final CommandLineParameters params;

    private GraphService graphService = null;

    public OTPConfigurator(CommandLineParameters params) {
        this.params = params;
    }
    
    private OTPServer server;

    /**
     * We could even do this at Configurator construct time (rather than lazy initializing), using
     * the inMemory param to create the right kind of GraphService ahead of time. However that would
     * create indexes even when only a build was going to happen.
     */
    public OTPServer getServer() {
        if (this.server == null) {
            this.server = new OTPServer(this.params, getGraphService());
        }
        return this.server;
    }
    
    /** Create a cached GraphService that will be shared between all OTP components. */
    public void makeGraphService(Graph graph) {
        /* Hand off graph in memory to server in a single-graph in-memory GraphServiceImpl. */
        if ((graph != null) && (this.params.inMemory || this.params.preFlight)) {
            try {
                FileInputStream graphConfiguration = new FileInputStream(this.params.graphConfigFile);
                Preferences config = new PropertiesPreferences(graphConfiguration);
                this.graphService = new GraphServiceBeanImpl(graph, config);
            } catch (Exception e) {
                if (this.params.graphConfigFile != null) {
                    LOG.error("Can't read config file", e);
                }
                this.graphService = new GraphServiceBeanImpl(graph, null);
            }
        } else if (this.params.autoScan) {
            /* Create an auto-scan+reload GraphService */
            GraphServiceAutoDiscoverImpl graphService = new GraphServiceAutoDiscoverImpl();
            if (this.params.graphDirectory != null) {
                graphService.setPath(this.params.graphDirectory);
            }
            if (this.params.routerIds.size() > 0) {
                graphService.setDefaultRouterId(this.params.routerIds.get(0));
            }
            graphService.startup();
            this.graphService = graphService;
        } else {
            /* Create a conventional GraphService that loads graphs from disk. */
            GraphServiceImpl graphService = new GraphServiceImpl();
            if (this.params.graphDirectory != null) {
                graphService.setPath(this.params.graphDirectory);
            }
            if (this.params.routerIds.size() > 0) {
                graphService.setDefaultRouterId(this.params.routerIds.get(0));
                graphService.autoRegister = this.params.routerIds;
            }
            graphService.startup();
            this.graphService = graphService;
        }
    }
    
    /** Return the cached, shared GraphService, making one as needed. */
    public GraphService getGraphService() {
        if (this.graphService == null) {
            makeGraphService(null);
        }
        return this.graphService;
    }

    public GraphBuilderTask builderFromParameters() {
        if ((this.params.build == null) || this.params.build.isEmpty()) { return null; }
        LOG.info("Wiring up and configuring graph builder task.");
        GraphBuilderTask graphBuilder = new GraphBuilderTask();
        List<File> gtfsFiles = Lists.newArrayList();
        List<File> osmFiles = Lists.newArrayList();
        File configFile = null;
        /*
         * For now this is adding files from all directories listed, rather than building multiple
         * graphs.
         */
        for (File dir : this.params.build) {
            LOG.info("Searching for graph builder input files in {}", dir);
            if (!dir.isDirectory() && dir.canRead()) {
                LOG.error("'{}' is not a readable directory.", dir);
                continue;
            }
            graphBuilder.setPath(dir);
            for (File file : dir.listFiles()) {
                switch (InputFileType.forFile(file)) {
                    case GTFS:
                        LOG.info("Found GTFS file {}", file);
                        gtfsFiles.add(file);
                        break;
                    case OSM:
                        LOG.info("Found OSM file {}", file);
                        osmFiles.add(file);
                        break;
                    case CONFIG:
                        if (!this.params.noEmbedConfig) {
                            LOG.info("Found CONFIG file {}", file);
                            configFile = file;
                        }
                        break;
                    case OTHER:
                        LOG.debug("Skipping file '{}'", file);
                }
            }
        }
        boolean hasOSM = !(osmFiles.isEmpty() || this.params.noStreets);
        boolean hasGTFS = !(gtfsFiles.isEmpty() || this.params.noTransit);
        if (!(hasOSM || hasGTFS)) {
            LOG.error("Found no input files from which to build a graph in {}", this.params.build.toString());
            return null;
        }
        if (hasOSM) {
            List<OpenStreetMapProvider> osmProviders = Lists.newArrayList();
            for (File osmFile : osmFiles) {
                OpenStreetMapProvider osmProvider = new AnyFileBasedOpenStreetMapProviderImpl(osmFile);
                osmProviders.add(osmProvider);
            }
            OpenStreetMapGraphBuilderImpl osmBuilder = new OpenStreetMapGraphBuilderImpl(osmProviders);
            DefaultWayPropertySetSource defaultWayPropertySetSource = new DefaultWayPropertySetSource();
            osmBuilder.setDefaultWayPropertySetSource(defaultWayPropertySetSource);
            osmBuilder.skipVisibility = this.params.skipVisibility;
            graphBuilder.addGraphBuilder(osmBuilder);
            graphBuilder.addGraphBuilder(new PruneFloatingIslands());
        }
        if (hasGTFS) {
            List<GtfsBundle> gtfsBundles = Lists.newArrayList();
            for (File gtfsFile : gtfsFiles) {
                GtfsBundle gtfsBundle = new GtfsBundle(gtfsFile);
                gtfsBundle.setTransfersTxtDefinesStationPaths(this.params.useTransfersTxt);
                if (!this.params.noParentStopLinking) {
                    gtfsBundle.linkStopsToParentStations = true;
                }
                gtfsBundle.parentStationTransfers = this.params.parentStationTransfers;
                gtfsBundles.add(gtfsBundle);
            }
            GtfsGraphBuilderImpl gtfsBuilder = new GtfsGraphBuilderImpl(gtfsBundles);
            graphBuilder.addGraphBuilder(gtfsBuilder);
            // When using the long distance path service, or when there is no street data,
            // link stops to each other based on distance only, unless user has requested linking
            // based on transfers.txt or the street network (if available).
            if ((!hasOSM) || this.params.longDistance) {
                if (!this.params.useTransfersTxt) {
                    if (!hasOSM || !this.params.useStreetsForLinking) {
                        graphBuilder.addGraphBuilder(new StreetlessStopLinker());
                    }
                }
            }
            if (hasOSM) {
                graphBuilder.addGraphBuilder(new TransitToTaggedStopsGraphBuilderImpl());
                graphBuilder.addGraphBuilder(new TransitToStreetNetworkGraphBuilderImpl());
                // The stops can be linked to each other once they have links to the street network.
                if (this.params.longDistance && this.params.useStreetsForLinking && !this.params.useTransfersTxt) {
                    graphBuilder.addGraphBuilder(new StreetfulStopLinker());
                }
            }
            gtfsBuilder.setFareServiceFactory(new DefaultFareServiceFactory());
        }
        if (configFile != null) {
            EmbeddedConfigGraphBuilderImpl embeddedConfigBuilder = new EmbeddedConfigGraphBuilderImpl();
            embeddedConfigBuilder.propertiesFile = configFile;
            graphBuilder.addGraphBuilder(embeddedConfigBuilder);
        }
        if (this.params.elevation) {
            File cacheDirectory = new File(this.params.cacheDirectory, "ned");
            ElevationGridCoverageFactory gcf = new NEDGridCoverageFactoryImpl(cacheDirectory);
            GraphBuilder elevationBuilder = new ElevationGraphBuilderImpl(gcf);
            graphBuilder.addGraphBuilder(elevationBuilder);
        }
        graphBuilder.serializeGraph = (!this.params.inMemory) || this.params.preFlight;
        return graphBuilder;
    }
    
    public GrizzlyServer serverFromParameters() {
        if (this.params.server) {
            GrizzlyServer server = new GrizzlyServer(this.params, getServer());
            return server;
        } else {
            return null;
        }
    }

    public GraphVisualizer visualizerFromParameters() {
        if (this.params.visualize) {
            // FIXME get OTPServer into visualizer.
            getServer();
            GraphVisualizer visualizer = new GraphVisualizer(getGraphService());
            return visualizer;
        } else {
            return null;
        }
    }
    
    private static enum InputFileType {
        GTFS, OSM, CONFIG, OTHER;
        public static InputFileType forFile(File file) {
            String name = file.getName();
            if (name.endsWith(".zip")) {
                try {
                    ZipFile zip = new ZipFile(file);
                    ZipEntry stopTimesEntry = zip.getEntry("stop_times.txt");
                    zip.close();
                    if (stopTimesEntry != null) { return GTFS; }
                } catch (Exception e) { /* fall through */
                }
            }
            if (name.endsWith(".pbf")) { return OSM; }
            if (name.endsWith(".osm")) { return OSM; }
            if (name.endsWith(".osm.xml")) { return OSM; }
            if (name.equals("Embed.properties")) { return CONFIG; }
            return OTHER;
        }
    }
    
}

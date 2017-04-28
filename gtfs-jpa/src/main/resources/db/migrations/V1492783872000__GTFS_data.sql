DELIMITER $$

DROP PROCEDURE IF EXISTS gtfs_data $$

-- Create the stored procedure to perform the idempotent migration
CREATE PROCEDURE gtfs_data()

  BEGIN
    CREATE TABLE IF NOT EXISTS `gtfs_agencies` (
      `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT,
      `o_agency_id` VARCHAR(255) NOT NULL,
      `fare_url`    VARCHAR(255)          DEFAULT NULL,
      `lang`        VARCHAR(255)          DEFAULT NULL,
      `name`        VARCHAR(255) NOT NULL,
      `phone`       VARCHAR(255)          DEFAULT NULL,
      `timezone`    VARCHAR(255) NOT NULL,
      `url`         VARCHAR(255) NOT NULL,
      PRIMARY KEY (`id`),
      KEY `o_agency_idx` (`o_agency_id`)
    );

    CREATE TABLE IF NOT EXISTS `gtfs_calendar_dates` (
      `id`             BIGINT(20)   NOT NULL AUTO_INCREMENT,
      `date`           DATETIME     NOT NULL,
      `exception_type` INT(11)               DEFAULT NULL,
      `o_service_id`   VARCHAR(255) NOT NULL,
      PRIMARY KEY (`id`)
    );

    CREATE TABLE IF NOT EXISTS `gtfs_calendars` (
      `id`           BIGINT(20)   NOT NULL AUTO_INCREMENT,
      `end_date`     DATETIME              DEFAULT NULL,
      `friday`       BIT(1)                DEFAULT NULL,
      `monday`       BIT(1)                DEFAULT NULL,
      `saturday`     BIT(1)                DEFAULT NULL,
      `o_service_id` VARCHAR(255) NOT NULL,
      `start_date`   DATETIME              DEFAULT NULL,
      `sunday`       BIT(1)                DEFAULT NULL,
      `thursday`     BIT(1)                DEFAULT NULL,
      `tuesday`      BIT(1)                DEFAULT NULL,
      `wednesday`    BIT(1)                DEFAULT NULL,
      PRIMARY KEY (`id`)
    );

    CREATE TABLE IF NOT EXISTS `gtfs_fares` (
      `id`        BIGINT(20)   NOT NULL AUTO_INCREMENT,
      `o_fare_id` VARCHAR(255) NOT NULL,
      PRIMARY KEY (`id`)
    );

    CREATE TABLE IF NOT EXISTS `gtfs_feeds` (
      `id`             BIGINT(20)   NOT NULL AUTO_INCREMENT,
      `end_date`       DATETIME              DEFAULT NULL,
      `language`       VARCHAR(255) NOT NULL,
      `publisher_name` VARCHAR(255) NOT NULL,
      `publisher_url`  VARCHAR(255) NOT NULL,
      `start_date`     DATETIME              DEFAULT NULL,
      `version`        VARCHAR(255)          DEFAULT NULL,
      PRIMARY KEY (`id`)
    );

    CREATE TABLE IF NOT EXISTS `gtfs_shapes` (
      `id`         BIGINT(20)   NOT NULL AUTO_INCREMENT,
      `distance`   DOUBLE                DEFAULT NULL,
      `latitude`   DOUBLE                DEFAULT NULL,
      `longitude`  DOUBLE                DEFAULT NULL,
      `sequence`   BIGINT(20)            DEFAULT NULL,
      `o_shape_id` VARCHAR(255) NOT NULL,
      PRIMARY KEY (`id`),
      KEY `o_shape_idx` (`o_shape_id`)
    );

    CREATE TABLE IF NOT EXISTS `gtfs_stops` (
      `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT,
      `code`            VARCHAR(255)          DEFAULT NULL,
      `description`     VARCHAR(255)          DEFAULT NULL,
      `latitude`        DOUBLE                DEFAULT NULL,
      `longitude`       DOUBLE                DEFAULT NULL,
      `location_type`   INT(11)               DEFAULT NULL,
      `name`            VARCHAR(255) NOT NULL,
      `parent_station`  INT(11)               DEFAULT NULL,
      `o_stop_id`       VARCHAR(255) NOT NULL,
      `time_zone`       VARCHAR(255)          DEFAULT NULL,
      `url`             VARCHAR(255)          DEFAULT NULL,
      `wheelchair_type` INT(11)               DEFAULT NULL,
      `zone`            VARCHAR(255)          DEFAULT NULL,
      PRIMARY KEY (`id`),
      KEY `o_stop_idx` (`o_stop_id`)
    );

    CREATE TABLE IF NOT EXISTS `gtfs_timezone` (
      `id`   BIGINT(20)   NOT NULL AUTO_INCREMENT,
      `zone` VARCHAR(255) NOT NULL,
      PRIMARY KEY (`id`),
      UNIQUE KEY `UK_ir95pwjy83lhkcqh1q5i68fb1` (`zone`)
    );

    CREATE TABLE IF NOT EXISTS `gtfs_fare_attributes` (
      `id`                BIGINT(20) NOT NULL AUTO_INCREMENT,
      `currency_type`     INT(11)             DEFAULT NULL,
      `payment_type`      INT(11)             DEFAULT NULL,
      `price`             DOUBLE     NOT NULL,
      `transfer_duration` DOUBLE              DEFAULT NULL,
      `transfer_type`     INT(11)             DEFAULT NULL,
      `fare`              BIGINT(20)          DEFAULT NULL,
      PRIMARY KEY (`id`),
      KEY `FK46ekqpb7on45u8ynod9cp6g2l` (`fare`),
      CONSTRAINT `FK46ekqpb7on45u8ynod9cp6g2l` FOREIGN KEY (`fare`) REFERENCES `gtfs_fares` (`id`)
    );

    CREATE TABLE IF NOT EXISTS `gtfs_routes` (
      `id`             BIGINT(20)   NOT NULL AUTO_INCREMENT,
      `description`    VARCHAR(255)          DEFAULT NULL,
      `hex_path_color` VARCHAR(255)          DEFAULT NULL,
      `hex_text_color` VARCHAR(255)          DEFAULT NULL,
      `long_name`      VARCHAR(255) NOT NULL,
      `o_route_id`     VARCHAR(255)          DEFAULT NULL,
      `short_name`     VARCHAR(255) NOT NULL,
      `route_type`     INT(11)               DEFAULT NULL,
      `url`            VARCHAR(255)          DEFAULT NULL,
      `agency`         BIGINT(20)   NOT NULL,
      PRIMARY KEY (`id`),
      KEY `o_route_idx` (`o_route_id`),
      KEY `FKhbai6ir6652ua2ky0akb63g6l` (`agency`),
      CONSTRAINT `FKhbai6ir6652ua2ky0akb63g6l` FOREIGN KEY (`agency`) REFERENCES `gtfs_agencies` (`id`)
    );

    CREATE TABLE IF NOT EXISTS `gtfs_fare_rules` (
      `id`               BIGINT(20) NOT NULL AUTO_INCREMENT,
      `contains_zone`    VARCHAR(255)        DEFAULT NULL,
      `destination_zone` VARCHAR(255)        DEFAULT NULL,
      `origin_zone`      VARCHAR(255)        DEFAULT NULL,
      `fare`             BIGINT(20)          DEFAULT NULL,
      `route`            BIGINT(20) NOT NULL,
      PRIMARY KEY (`id`),
      KEY `FKa9ohxp7h1t5qjmf2cvjjqwpc4` (`fare`),
      KEY `FKgf1hyvs9xhnd5jebpnty4qd81` (`route`),
      CONSTRAINT `FKa9ohxp7h1t5qjmf2cvjjqwpc4` FOREIGN KEY (`fare`) REFERENCES `gtfs_fares` (`id`),
      CONSTRAINT `FKgf1hyvs9xhnd5jebpnty4qd81` FOREIGN KEY (`route`) REFERENCES `gtfs_routes` (`id`)
    );

    CREATE TABLE IF NOT EXISTS `gtfs_transfers` (
      `id`            BIGINT(20) NOT NULL AUTO_INCREMENT,
      `min_transfer`  BIGINT(20)          DEFAULT NULL,
      `transfer_type` INT(11)             DEFAULT NULL,
      `from_stop`     BIGINT(20) NOT NULL,
      `to_stop`       BIGINT(20) NOT NULL,
      PRIMARY KEY (`id`),
      KEY `FK4h5399xm8x9gcaqk0tvilurjb` (`from_stop`),
      KEY `FK2r5y3bdbsow7kj86sopjwhqk4` (`to_stop`),
      CONSTRAINT `FK2r5y3bdbsow7kj86sopjwhqk4` FOREIGN KEY (`to_stop`) REFERENCES `gtfs_stops` (`id`),
      CONSTRAINT `FK4h5399xm8x9gcaqk0tvilurjb` FOREIGN KEY (`from_stop`) REFERENCES `gtfs_stops` (`id`)
    );

    CREATE TABLE IF NOT EXISTS `gtfs_trips` (
      `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT,
      `block_id`        INT(11)               DEFAULT NULL,
      `direction_type`  INT(11)               DEFAULT NULL,
      `headsign`        VARCHAR(255)          DEFAULT NULL,
      `o_service_id`    VARCHAR(255) NOT NULL,
      `short_name`      VARCHAR(255)          DEFAULT NULL,
      `o_trip_id`       VARCHAR(255) NOT NULL,
      `wheelchair_type` INT(11)               DEFAULT NULL,
      `route`           BIGINT(20)   NOT NULL,
      PRIMARY KEY (`id`),
      KEY `o_trip_idx` (`o_trip_id`),
      KEY `FK44akupcaq2dr7eajgolqpv869` (`route`),
      CONSTRAINT `FK44akupcaq2dr7eajgolqpv869` FOREIGN KEY (`route`) REFERENCES `gtfs_routes` (`id`)
    );

    CREATE TABLE IF NOT EXISTS `gtfs_stop_times` (
      `id`                      BIGINT(20) NOT NULL AUTO_INCREMENT,
      `arrival_time`            TIME       NOT NULL,
      `departure_time`          TIME       NOT NULL,
      `drop_off_type`           INT(11)             DEFAULT NULL,
      `pick_up_time`            INT(11)             DEFAULT NULL,
      `shape_distance_traveled` DOUBLE              DEFAULT NULL,
      `stop_headsign`           VARCHAR(255)        DEFAULT NULL,
      `stop_sequence`           INT(11)             DEFAULT NULL,
      `stop`                    BIGINT(20) NOT NULL,
      `trip`                    BIGINT(20) NOT NULL,
      PRIMARY KEY (`id`),
      KEY `FKjw1ce3xlonb87vkko974uarm9` (`stop`),
      KEY `FK2me9x4pyo4x65w0a1vcyx0iob` (`trip`),
      CONSTRAINT `FK2me9x4pyo4x65w0a1vcyx0iob` FOREIGN KEY (`trip`) REFERENCES `gtfs_trips` (`id`),
      CONSTRAINT `FKjw1ce3xlonb87vkko974uarm9` FOREIGN KEY (`stop`) REFERENCES `gtfs_stops` (`id`)
    );

    CREATE TABLE IF NOT EXISTS `gtfs_frequencies` (
      `id`           BIGINT(20) NOT NULL AUTO_INCREMENT,
      `end_time`     TIME       NOT NULL,
      `exact_time`   INT(11)             DEFAULT NULL,
      `headway_secs` BIGINT(20)          DEFAULT NULL,
      `start_time`   TIME       NOT NULL,
      `trip`         BIGINT(20) NOT NULL,
      PRIMARY KEY (`id`),
      KEY `FK7n8lkn4e85f0f1t0wj4mbvv0r` (`trip`),
      CONSTRAINT `FK7n8lkn4e85f0f1t0wj4mbvv0r` FOREIGN KEY (`trip`) REFERENCES `gtfs_trips` (`id`)
    );

    CREATE TABLE `gtfs_trips_gtfs_shapes` (
      `gtfs_trips_id` BIGINT(20) NOT NULL,
      `shapes_id`     BIGINT(20) NOT NULL,
      `sequence`      INT(11)    NOT NULL,
      PRIMARY KEY (`gtfs_trips_id`, `sequence`),
      KEY `FKs5kf5557bqm42u8vqlt8s480i` (`shapes_id`),
      CONSTRAINT `FKs5kf5557bqm42u8vqlt8s480i` FOREIGN KEY (`shapes_id`) REFERENCES `gtfs_shapes` (`id`),
      CONSTRAINT `FKs9arv5aw1kf3oiqsespd9m0ea` FOREIGN KEY (`gtfs_trips_id`) REFERENCES `gtfs_trips` (`id`)
    );

  END $$

-- Execute the stored procedure
CALL gtfs_data() $$

-- Drop the stored procedure when finished
DROP PROCEDURE IF EXISTS gtfs_data $$

-- Reset default delimiter
DELIMITER ;

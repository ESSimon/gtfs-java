package com.trein.gtfs.jpa.repository;

import com.everysens.rtls.commons.repository.GenericRepository;
import com.trein.gtfs.jpa.entity.Shape;

import java.util.List;

public interface ShapeRepository extends GenericRepository<Shape, Long> {

    List<Shape> findByShapeId(String shapeId);

}

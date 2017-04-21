package com.trein.gtfs.jpa.repository;

import com.trein.gtfs.jpa.entity.Shape;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShapeRepository extends JpaRepository<Shape, Long> {

    List<Shape> findByShapeId(String shapeId);

}

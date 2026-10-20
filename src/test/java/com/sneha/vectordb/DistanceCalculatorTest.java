package com.sneha.vectordb;

import com.sneha.vectordb.index.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DistanceCalculatorTest {
 @Test void euclideanDistance(){assertEquals(5.0,DistanceCalculator.distance(new double[]{0,0},new double[]{3,4},DistanceMetric.EUCLIDEAN),1e-9);}
 @Test void manhattanDistance(){assertEquals(7.0,DistanceCalculator.distance(new double[]{0,0},new double[]{3,4},DistanceMetric.MANHATTAN),1e-9);}
 @Test void identicalVectorsHaveZeroCosineDistance(){assertEquals(0.0,DistanceCalculator.distance(new double[]{1,2},new double[]{1,2},DistanceMetric.COSINE),1e-9);}
}

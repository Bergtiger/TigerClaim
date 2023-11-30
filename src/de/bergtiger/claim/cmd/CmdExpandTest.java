package de.bergtiger.claim.cmd;

import com.sk89q.worldedit.math.BlockVector2;
import de.bergtiger.claim.data.UnitePolygonsResult;
import de.bergtiger.claim.data.UnitePolygonsResultType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class CmdExpandTest {

    @Test
    @DisplayName("Erkennung, dass Polygone sich verbinden lassen")
    void test1() {
        //Arrange
        ArrayList<BlockVector2> alteRegionsBlockPolygon = new ArrayList<>();
        alteRegionsBlockPolygon.add(BlockVector2.at(0, 0));
        alteRegionsBlockPolygon.add(BlockVector2.at(0, 2));
        alteRegionsBlockPolygon.add(BlockVector2.at(2, 2));
        alteRegionsBlockPolygon.add(BlockVector2.at(2, 0));
        ArrayList<BlockVector2> markierungsBlockPolygon = new ArrayList<>();
        markierungsBlockPolygon.add(BlockVector2.at(1, 1));
        markierungsBlockPolygon.add(BlockVector2.at(1, 3));
        markierungsBlockPolygon.add(BlockVector2.at(3, 3));
        markierungsBlockPolygon.add(BlockVector2.at(3, 1));
        //Act
        UnitePolygonsResult result = CmdExpand.uniteTwoPolygons(alteRegionsBlockPolygon, markierungsBlockPolygon);
        //Assert
        assertThat(result.getResultType()).isEqualTo(UnitePolygonsResultType.BOUNDING_POLYGON_FOUND);
    }

    @Test
    @DisplayName("Erkennung, dass Polygone sich nicht berühren")
    void test2() {
        //Arrange
        ArrayList<BlockVector2> alteRegionsBlockPolygon = new ArrayList<>();
        alteRegionsBlockPolygon.add(BlockVector2.at(0, 0));
        alteRegionsBlockPolygon.add(BlockVector2.at(0, 2));
        alteRegionsBlockPolygon.add(BlockVector2.at(2, 2));
        alteRegionsBlockPolygon.add(BlockVector2.at(2, 0));
        ArrayList<BlockVector2> markierungsBlockPolygon = new ArrayList<>();
        markierungsBlockPolygon.add(BlockVector2.at(3, 3));
        markierungsBlockPolygon.add(BlockVector2.at(3, 5));
        markierungsBlockPolygon.add(BlockVector2.at(5, 5));
        markierungsBlockPolygon.add(BlockVector2.at(5, 3));
        //Act
        UnitePolygonsResult result = CmdExpand.uniteTwoPolygons(alteRegionsBlockPolygon, markierungsBlockPolygon);
        //Assert
        assertThat(result.getResultType()).isEqualTo(UnitePolygonsResultType.POLYGONS_DONT_INTERSECT_EACH_OTHER);
    }

    @Test
    @DisplayName("Erkennung, dass Polygone sich selbst überschneiden")
    void test3() {
        //Arrange
        ArrayList<BlockVector2> alteRegionsBlockPolygon = new ArrayList<>();
        alteRegionsBlockPolygon.add(BlockVector2.at(0, 0));
        alteRegionsBlockPolygon.add(BlockVector2.at(0, 2));
        alteRegionsBlockPolygon.add(BlockVector2.at(2, 0));
        alteRegionsBlockPolygon.add(BlockVector2.at(2, 2));
        ArrayList<BlockVector2> markierungsBlockPolygon = new ArrayList<>();
        markierungsBlockPolygon.add(BlockVector2.at(1, 1));
        markierungsBlockPolygon.add(BlockVector2.at(1, 3));
        markierungsBlockPolygon.add(BlockVector2.at(3, 1));
        markierungsBlockPolygon.add(BlockVector2.at(3, 3));
        //Act
        UnitePolygonsResult result = CmdExpand.uniteTwoPolygons(alteRegionsBlockPolygon, markierungsBlockPolygon);
        //Assert
        assertThat(result.getResultType()).isEqualTo(UnitePolygonsResultType.BOTH_POLYGONS_INTERSECT_THEMSELVES);
    }

    @Test
    @DisplayName("Erkennung, dass Polygone identisch sind, obwohl Reihenfolge ihrer Punkte unterschiedlich")
    void test4() {
        //Arrange
        ArrayList<BlockVector2> alteRegionsBlockPolygon = new ArrayList<>();
        alteRegionsBlockPolygon.add(BlockVector2.at(0, 0));
        alteRegionsBlockPolygon.add(BlockVector2.at(0, 2));
        alteRegionsBlockPolygon.add(BlockVector2.at(2, 2));
        alteRegionsBlockPolygon.add(BlockVector2.at(2, 0));
        ArrayList<BlockVector2> markierungsBlockPolygon = new ArrayList<>();
        markierungsBlockPolygon.add(BlockVector2.at(2, 0));
        markierungsBlockPolygon.add(BlockVector2.at(0, 0));
        markierungsBlockPolygon.add(BlockVector2.at(0, 2));
        markierungsBlockPolygon.add(BlockVector2.at(2, 2));
        //Act
        UnitePolygonsResult result = CmdExpand.uniteTwoPolygons(alteRegionsBlockPolygon, markierungsBlockPolygon);
        //Assert
        assertThat(result.getResultType()).isEqualTo(UnitePolygonsResultType.POLYGONS_ARE_EQUAL);
    }

    @Test
    @DisplayName("Erkennung, dass markierungsBlockPolygon innerhalb alteRegionsBlockPolygon liegt")
    void test5() {
        //Arrange
        ArrayList<BlockVector2> alteRegionsBlockPolygon = new ArrayList<>();
        alteRegionsBlockPolygon.add(BlockVector2.at(0, 0));
        alteRegionsBlockPolygon.add(BlockVector2.at(0, 4));
        alteRegionsBlockPolygon.add(BlockVector2.at(4, 4));
        alteRegionsBlockPolygon.add(BlockVector2.at(4, 0));
        ArrayList<BlockVector2> markierungsBlockPolygon = new ArrayList<>();
        markierungsBlockPolygon.add(BlockVector2.at(1, 1));
        markierungsBlockPolygon.add(BlockVector2.at(1, 3));
        markierungsBlockPolygon.add(BlockVector2.at(3, 3));
        markierungsBlockPolygon.add(BlockVector2.at(3, 1));
        //Act
        UnitePolygonsResult result = CmdExpand.uniteTwoPolygons(alteRegionsBlockPolygon, markierungsBlockPolygon);
        System.out.println("resultPolygon: " + result.getPolygon());
        //Assert
        assertThat(result.getResultType()).isEqualTo(UnitePolygonsResultType.SELECTION_IS_INSIDE_OLD_REGION);
    }
}
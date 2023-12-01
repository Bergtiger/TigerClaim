package Test;

import com.sk89q.worldedit.math.BlockVector2;
import de.bergtiger.claim.cmd.CmdExpand;
import de.bergtiger.claim.data.UnitePolygonsResult;
import de.bergtiger.claim.data.UnitePolygonsResultType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
        assertThat(result.containsGapsFromOldRegionAndSelection).isFalse();
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
        assertThat(result.containsGapsFromOldRegionAndSelection).isNull();
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
        assertThat(result.containsGapsFromOldRegionAndSelection).isNull();
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
        assertThat(result.containsGapsFromOldRegionAndSelection).isNull();
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
        assertThat(result.containsGapsFromOldRegionAndSelection).isNull();
    }

    @Test
    @DisplayName("Beispielpolygon (einfache Überschneidung) wird korrekt gebildet")
    void test6() {
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
        List<BlockVector2> ergebnisPolygon = result.getPolygon();
        //Assert
        ArrayList<BlockVector2> sollErgebnisPolygon = new ArrayList<>();
        sollErgebnisPolygon.add(BlockVector2.at(0, 0));
        sollErgebnisPolygon.add(BlockVector2.at(0, 2));
        sollErgebnisPolygon.add(BlockVector2.at(1, 2));
        sollErgebnisPolygon.add(BlockVector2.at(1, 3));
        sollErgebnisPolygon.add(BlockVector2.at(3, 3));
        sollErgebnisPolygon.add(BlockVector2.at(3, 1));
        sollErgebnisPolygon.add(BlockVector2.at(2, 1));
        sollErgebnisPolygon.add(BlockVector2.at(2, 0));
        assertThat(ergebnisPolygon).containsExactlyInAnyOrderElementsOf(sollErgebnisPolygon);
        assertThat(result.containsGapsFromOldRegionAndSelection).isFalse();
    }

    @Test
    @DisplayName("Überschneidung der Polygone hat Loch in der Mitte -> Loch wird korrekt ignoriert")
    void test7() {
        //Arrange
        ArrayList<BlockVector2> alteRegionsBlockPolygon = new ArrayList<>();
        alteRegionsBlockPolygon.add(BlockVector2.at(0, 0));
        alteRegionsBlockPolygon.add(BlockVector2.at(0, 4));
        alteRegionsBlockPolygon.add(BlockVector2.at(8, 4));
        alteRegionsBlockPolygon.add(BlockVector2.at(8, 0));
        ArrayList<BlockVector2> markierungsBlockPolygon = new ArrayList<>();
        markierungsBlockPolygon.add(BlockVector2.at(2, 2));
        markierungsBlockPolygon.add(BlockVector2.at(2, 8));
        markierungsBlockPolygon.add(BlockVector2.at(6, 8));
        markierungsBlockPolygon.add(BlockVector2.at(6, 2));
        markierungsBlockPolygon.add(BlockVector2.at(4, 6));
        //Act
        UnitePolygonsResult result = CmdExpand.uniteTwoPolygons(alteRegionsBlockPolygon, markierungsBlockPolygon);
        List<BlockVector2> ergebnisPolygon = result.getPolygon();
        //Assert
        ArrayList<BlockVector2> sollErgebnisPolygon = new ArrayList<>();
        sollErgebnisPolygon.add(BlockVector2.at(0, 0));
        sollErgebnisPolygon.add(BlockVector2.at(0, 4));
        sollErgebnisPolygon.add(BlockVector2.at(2, 4));
        sollErgebnisPolygon.add(BlockVector2.at(2, 8));
        sollErgebnisPolygon.add(BlockVector2.at(6, 8));
        sollErgebnisPolygon.add(BlockVector2.at(6, 4));
        sollErgebnisPolygon.add(BlockVector2.at(8, 4));
        sollErgebnisPolygon.add(BlockVector2.at(8, 0));
        assertThat(ergebnisPolygon).containsExactlyInAnyOrderElementsOf(sollErgebnisPolygon);
        assertThat(result.containsGapsFromOldRegionAndSelection).isTrue();
    }

    @Test
    @DisplayName("alteRegionsBlockPolygon liegt in markierungsBlockPolygon und sie haben drei übereinanderliegende Kanten, freie Kante liegt südlich (Bug #9)")
    void test8() {
        //Arrange
        ArrayList<BlockVector2> alteRegionsBlockPolygon = new ArrayList<>();
        alteRegionsBlockPolygon.add(BlockVector2.at(0, 0));
        alteRegionsBlockPolygon.add(BlockVector2.at(0, 2));
        alteRegionsBlockPolygon.add(BlockVector2.at(2, 2));
        alteRegionsBlockPolygon.add(BlockVector2.at(2, 0));
        ArrayList<BlockVector2> markierungsBlockPolygon = new ArrayList<>();
        markierungsBlockPolygon.add(BlockVector2.at(0, 0));
        markierungsBlockPolygon.add(BlockVector2.at(0, 3));
        markierungsBlockPolygon.add(BlockVector2.at(2, 3));
        markierungsBlockPolygon.add(BlockVector2.at(2, 0));
        //Act
        UnitePolygonsResult result = CmdExpand.uniteTwoPolygons(alteRegionsBlockPolygon, markierungsBlockPolygon);
        List<BlockVector2> ergebnisPolygon = result.getPolygon();
        //Assert
        ArrayList<BlockVector2> sollErgebnisPolygon = new ArrayList<>();
        sollErgebnisPolygon.add(BlockVector2.at(0, 0));
        sollErgebnisPolygon.add(BlockVector2.at(0, 3));
        sollErgebnisPolygon.add(BlockVector2.at(2, 3));
        sollErgebnisPolygon.add(BlockVector2.at(2, 0));
        assertThat(ergebnisPolygon).containsExactlyInAnyOrderElementsOf(sollErgebnisPolygon);
        assertThat(result.containsGapsFromOldRegionAndSelection).isFalse();
    }

    @Test
    @DisplayName("Drehung von test8 (funktioniert komischerweise schon, frei Kante liegt östlich)")
    void test9() {
        //Arrange
        ArrayList<BlockVector2> alteRegionsBlockPolygon = new ArrayList<>();
        alteRegionsBlockPolygon.add(BlockVector2.at(0, 0));
        alteRegionsBlockPolygon.add(BlockVector2.at(0, 2));
        alteRegionsBlockPolygon.add(BlockVector2.at(2, 2));
        alteRegionsBlockPolygon.add(BlockVector2.at(2, 0));
        ArrayList<BlockVector2> markierungsBlockPolygon = new ArrayList<>();
        markierungsBlockPolygon.add(BlockVector2.at(0, 0));
        markierungsBlockPolygon.add(BlockVector2.at(0, 2));
        markierungsBlockPolygon.add(BlockVector2.at(3, 2));
        markierungsBlockPolygon.add(BlockVector2.at(3, 0));
        //Act
        UnitePolygonsResult result = CmdExpand.uniteTwoPolygons(alteRegionsBlockPolygon, markierungsBlockPolygon);
        List<BlockVector2> ergebnisPolygon = result.getPolygon();
        //Assert
        ArrayList<BlockVector2> sollErgebnisPolygon = new ArrayList<>();
        sollErgebnisPolygon.add(BlockVector2.at(0, 0));
        sollErgebnisPolygon.add(BlockVector2.at(0, 2));
        sollErgebnisPolygon.add(BlockVector2.at(3, 2));
        sollErgebnisPolygon.add(BlockVector2.at(3, 0));
        assertThat(ergebnisPolygon).containsExactlyInAnyOrderElementsOf(sollErgebnisPolygon);
        assertThat(result.containsGapsFromOldRegionAndSelection).isFalse();
    }
}
package Test;

import com.sk89q.worldedit.math.Vector2;
import de.bergtiger.claim.data.ClaimUtils;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static de.bergtiger.claim.data.ClaimUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ClaimUtilsTest {
    @Test
    @DisplayName("Polygon läuft gegen den Uhrzeigersinn")
    void test1() {
        //Arrange
        ArrayList<Vector2> alteRegionsBlockPolygon = new ArrayList<>();
        alteRegionsBlockPolygon.add(Vector2.at(0, 0));
        alteRegionsBlockPolygon.add(Vector2.at(0, 2));
        alteRegionsBlockPolygon.add(Vector2.at(2, 2));
        alteRegionsBlockPolygon.add(Vector2.at(2, 0));
        //Act
        boolean polygonLäuftImUhrzeigersinn = verläuftPolygonImUhrzeigersinn(alteRegionsBlockPolygon);
        //Assert
        assertThat(polygonLäuftImUhrzeigersinn).isFalse();
    }

    @Test
    @DisplayName("funktioniert liegtStreckeAWeiterRechtsAlsStreckeB?")
    void test2() {
        //Arrange
        Vector2 start = Vector2.at(0,0);
        Vector2 verzweigung = Vector2.at(1,0);
        Vector2 zielA = Vector2.at(1,1);
        Vector2 zielB = Vector2.at(2,0);
        //Act
        boolean liegtStreckeAWeiterRechtsAlsStreckeB = liegtStreckeAWeiterRechtsAlsStreckeB(start, verzweigung, zielA, zielB);
        //Assert
        assertThat(liegtStreckeAWeiterRechtsAlsStreckeB).isTrue();
    }

    @Test
    @DisplayName("funktioniert liegtStreckeAWeiterRechtsAlsStreckeB?")
    void test3() {
        //Arrange
        Vector2 start = Vector2.at(0,1);
        Vector2 verzweigung = Vector2.at(0,2);
        Vector2 zielA = Vector2.at(0,3);
        Vector2 zielB = Vector2.at(2,2);
        //Act
        boolean liegtStreckeAWeiterRechtsAlsStreckeB = liegtStreckeAWeiterRechtsAlsStreckeB(start, verzweigung, zielA, zielB);
        //Assert
        assertThat(liegtStreckeAWeiterRechtsAlsStreckeB).isTrue();
    }

    @Test
    @DisplayName("funktioniert winkelZwischenZweiVector2InDegrees?")
    void test4() {
        //Arrange
        Vector2 v1 = Vector2.at(0,1);
        Vector2 v2 = Vector2.at(1,0);
        //Act
        double winkel = winkelZwischenZweiVector2InDegrees(v1, v2);
        //Assert
        assertThat(winkel).isCloseTo(90.0, Offset.offset(0.000001));
    }

    @Test
    @DisplayName("funktioniert winkelZwischenZweiVector2InDegrees?")
    void test5() {
        //Arrange
        Vector2 v1 = Vector2.at(1,0);
        Vector2 v2 = Vector2.at(0,1);
        //Act
        double winkel = winkelZwischenZweiVector2InDegrees(v1, v2);
        //Assert
        assertThat(winkel).isCloseTo(-90.0, Offset.offset(0.000001));
    }

    @Test
    @DisplayName("funktioniert winkelZwischenZweiVector2InDegrees?")
    void test6() {
        //Arrange
        Vector2 v1 = Vector2.at(0,-1);
        Vector2 v2 = Vector2.at(0,1);
        //Act
        double winkel = winkelZwischenZweiVector2InDegrees(v1, v2);
        //Assert
        assertThat(winkel).isCloseTo(-180.0, Offset.offset(0.000001));
    }
}
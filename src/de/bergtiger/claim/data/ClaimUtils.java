package de.bergtiger.claim.data;

import static de.bergtiger.claim.data.language.Cons.ID;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.bergtiger.claim.data.language.Lang;
import de.bergtiger.claim.data.logger.TigerLogger;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ClaimUtils {

	public static String arrayToString(String[] args, int beginn) {
		if (args != null) {
			if (beginn < 0)
				beginn = 0;
			String s = "";
			for (int i = beginn; i < args.length; i++) {
				if (s.length() > 0)
					s += " ";
				s += args[i];
			}
			return s;
		}
		return null;
	}
	
	public static String buildRegionHover(ProtectedRegion r) {
		if(r != null)
			return Lang.LIST_HOVER.replace(ID, r.getId());
		return null;
	}

	public static double getArea(ProtectedRegion region) {
		if(region != null) {
			if (region instanceof ProtectedPolygonalRegion polyRegion) {
				double summe = 0;
				BlockVector2 letzterPunkt = null;
				for (int i = 0; i < polyRegion.getPoints().size(); i++) {
					BlockVector2 punkt = polyRegion.getPoints().get(i);
					if (i != 0) {
						summe = summe + letzterPunkt.getBlockX() * punkt.getBlockZ() - letzterPunkt.getBlockZ() * punkt.getBlockX();
					}
					letzterPunkt = punkt;
				}
				BlockVector2 ersterPunkt = polyRegion.getPoints().get(0);
				summe = summe + letzterPunkt.getBlockX() * ersterPunkt.getBlockZ() - letzterPunkt.getBlockZ() * ersterPunkt.getBlockX();
				double scharfeFläche = Math.abs(summe / 2.0);
				return round(flächeEinesPixelPolygons(scharfePolgonFläche(polyRegion.getPoints()), polyRegion.getPoints()),8);
			} else {
				//Rechtecks-Grundfläche
				double area = (1 + Math.abs((region.getMinimumPoint().getX() - region.getMaximumPoint().getX()))) *
						(1 + Math.abs(region.getMinimumPoint().getZ() - region.getMaximumPoint().getZ()));
				TigerLogger.log(Level.INFO, "ClaimUtils: Area: " + area);
				return area;
			}
		}
		return 0.0;
	}

	public static double getArea(Region region) {
		if(region != null) {
			if (region instanceof Polygonal2DRegion polyRegion) {
				double summe = 0;
				BlockVector2 letzterPunkt = null;
				for (int i = 0; i < polyRegion.getPoints().size(); i++) {
					BlockVector2 punkt = polyRegion.getPoints().get(i);
					if (i != 0) {
						summe = summe + letzterPunkt.getBlockX() * punkt.getBlockZ() - letzterPunkt.getBlockZ() * punkt.getBlockX();
					}
					letzterPunkt = punkt;
				}
				BlockVector2 ersterPunkt = polyRegion.getPoints().get(0);
				summe = summe + letzterPunkt.getBlockX() * ersterPunkt.getBlockZ() - letzterPunkt.getBlockZ() * ersterPunkt.getBlockX();
				double scharfeFläche = Math.abs(summe / 2.0);
				return flächeEinesPixelPolygons(scharfePolgonFläche(polyRegion.getPoints()), polyRegion.getPoints());
			} else {
				//Rechtecks-Grundfläche
				double area = (1 + Math.abs((region.getMinimumPoint().getX() - region.getMaximumPoint().getX()))) *
						(1 + Math.abs(region.getMinimumPoint().getZ() - region.getMaximumPoint().getZ()));
				TigerLogger.log(Level.INFO, "ClaimUtils: Area: " + area);
				return area;
			}
		}
		return 0.0;
	}

	public static double scharfePolgonFläche (List<BlockVector2> eckpunkteGanz) {
		double summe = 0;
		BlockVector2 letzterPunkt = null;
		for (int i = 0; i < eckpunkteGanz.size(); i++) {
			BlockVector2 punkt = eckpunkteGanz.get(i);
			if (i != 0) {
				summe = summe + letzterPunkt.getBlockX() * punkt.getBlockZ() - letzterPunkt.getBlockZ() * punkt.getBlockX();
			}
			letzterPunkt = punkt;
		}
		BlockVector2 ersterPunkt = eckpunkteGanz.get(0);
		summe = summe + letzterPunkt.getBlockX() * ersterPunkt.getBlockZ() - letzterPunkt.getBlockZ() * ersterPunkt.getBlockX();
		double scharfeFläche = Math.abs(summe / 2.0);
		return scharfeFläche;
	}

	public static double scharfePolgonFläche (ArrayList<Vector2> eckpunkte) {
		double summe = 0;
		Vector2 letzterPunkt = null;
		for (int i = 0; i < eckpunkte.size(); i++) {
			Vector2 punkt = eckpunkte.get(i);
			if (i != 0) {
				summe = summe + letzterPunkt.getX() * punkt.getZ() - letzterPunkt.getZ() * punkt.getX();
			}
			letzterPunkt = punkt;
		}
		Vector2 ersterPunkt = eckpunkte.get(0);
		summe = summe + letzterPunkt.getX() * ersterPunkt.getZ() - letzterPunkt.getZ() * ersterPunkt.getX();
		double scharfeFläche = Math.abs(summe / 2.0);
		return scharfeFläche;
	}

	public static double flächeEinesPixelPolygons(double scharfeFläche, List<BlockVector2> eckpunkteGanz) {
		boolean ausgaben = false;
		ArrayList<Vector2> eckpunkte = new ArrayList<>();
		for (BlockVector2 blockVector2 : eckpunkteGanz) {
			eckpunkte.add(Vector2.at( blockVector2.getX() + 0.5,blockVector2.getZ() + 0.5));
		}

		Boolean imUhrzeigerSinn = verläuftPolygonImUhrzeigersinn(eckpunkte);
		if (imUhrzeigerSinn == null) {
			System.out.println("ERROR: PolyRegion überschneidet sich selbst.");
			return -1.0;
		}
		if (ausgaben) System.out.println("imUhrzeigerSinn: " + imUhrzeigerSinn);

		double richtigeFläche = scharfeFläche;
		Vector2 letzterPunkt = eckpunkte.get(eckpunkte.size() - 1);
		//Für alle Kanten:
		for (int i = 0; i < eckpunkte.size(); i++) {
			Vector2 punkt = eckpunkte.get(i);
			Vector2 nächsterPunkt = eckpunkte.get(0);
			if (i < eckpunkte.size() - 1) {
				nächsterPunkt = eckpunkte.get(i+1);
			}
			double deltaX = Math.abs(punkt.getX() - letzterPunkt.getX());
			double deltaZ = Math.abs(punkt.getZ() - letzterPunkt.getZ());
			boolean xIstSchnellereRichtung = deltaX >= deltaZ;
			int sgnXSteigung = -1;
			if (punkt.getX() > letzterPunkt.getX()) {
				sgnXSteigung = 1;
			}
			int sgnZSteigung = -1;
			if (punkt.getZ() > letzterPunkt.getZ()) {
				sgnZSteigung = 1;
			}

			ArrayList<BlockVector2> pixelDieKanteSchneiden = new ArrayList<>();
			int letzterLangsamerWertInt = 0;
			int langsamerWertInt = 0;
			double langsamerWert = 0;
			double steigung;
			if (xIstSchnellereRichtung) {
				steigung = deltaZ / deltaX;
			} else {
				steigung = deltaX / deltaZ;
			}
			int k = 0;
			//Für alle Blöcke, die eine Kante schneidet:
			for (int j = 0; j <= Math.max(deltaX,deltaZ); j++) {
				double l = j + 0.5;
				langsamerWert = steigung * l + 0.5;
				langsamerWertInt = (int) Math.floor(langsamerWert);
				if (j != 0) {
					if ((double) langsamerWertInt != langsamerWert) {
						//A
						BlockVector2 pixelAnKante = null;
						if (xIstSchnellereRichtung) {
							pixelAnKante = BlockVector2.at(
									(int) (letzterPunkt.getX() - 0.5) + sgnXSteigung * j,
									(int) (letzterPunkt.getZ() - 0.5) + sgnZSteigung * langsamerWertInt);
							pixelDieKanteSchneiden.add(pixelAnKante);
						} else {
							pixelAnKante = BlockVector2.at(
									(int) (letzterPunkt.getX() - 0.5) + sgnXSteigung * langsamerWertInt,
									(int) (letzterPunkt.getZ() - 0.5) + sgnZSteigung * j);
							pixelDieKanteSchneiden.add(pixelAnKante);

						}
						double flächenErgänzung = polygonRandPixelFlächenErgänzung(eckpunkteGanz,pixelAnKante,imUhrzeigerSinn,letzterPunkt,punkt,nächsterPunkt);
						richtigeFläche = richtigeFläche + flächenErgänzung;
						if (ausgaben) System.out.println("Kante vor Punkt " + i + ", Block " + k + " : x: " +
								pixelAnKante.getX() + "; z: " + pixelAnKante.getZ() + " : A   -    flächenErgänzung: " + flächenErgänzung);
						k++;
					}
					if (letzterLangsamerWertInt < langsamerWertInt && steigung != 0.0) {
						//B
						BlockVector2 pixelAnKante = null;
						if (xIstSchnellereRichtung) {
							pixelAnKante = BlockVector2.at(
									(int) (letzterPunkt.getX() - 0.5) + sgnXSteigung * j,
									(int) (letzterPunkt.getZ() - 0.5) + sgnZSteigung * letzterLangsamerWertInt);
							pixelDieKanteSchneiden.add(pixelAnKante);
						} else {
							pixelAnKante = BlockVector2.at(
									(int) (letzterPunkt.getX() - 0.5) + sgnXSteigung * letzterLangsamerWertInt,
									(int) (letzterPunkt.getZ() - 0.5) + sgnZSteigung * j);
							pixelDieKanteSchneiden.add(pixelAnKante);
						}
						double flächenErgänzung = polygonRandPixelFlächenErgänzung(eckpunkteGanz,pixelAnKante,imUhrzeigerSinn,letzterPunkt,punkt,nächsterPunkt);
						richtigeFläche = richtigeFläche + flächenErgänzung;
						if (ausgaben) System.out.println("Kante vor Punkt " + i + ", Block " + k + " : x: " +
								pixelAnKante.getX() + "; z: " + pixelAnKante.getZ() + " : B   -    flächenErgänzung: " + flächenErgänzung);
						k++;
					}
				}
				letzterLangsamerWertInt = langsamerWertInt;
			}
			letzterPunkt = punkt;
		}
		return richtigeFläche;
	}

	private static double polygonRandPixelFlächenErgänzung(
			List<BlockVector2> eckpunkteGanz, BlockVector2 pixelPos, boolean uhrzeigersinn, Vector2 ecke0, Vector2 ecke1, Vector2 ecke2) {
		boolean ausgaben = false;
		boolean ecke1LiegtInPixel = pixelPos.equals(BlockVector2.at((int)(ecke1.getX() - 0.5),(int)(ecke1.getZ() - 0.5)));
		Vector2 kante1 = ecke1.subtract(ecke0);
		Vector2 kante2 = ecke2.subtract(ecke1);
		boolean linksKnick = kante1.getX() * kante2.getZ() - kante1.getZ() * kante2.getX() < 0;
		if (ausgaben) System.out.println("linksKnick: " + linksKnick + "; pixelPos: (" + pixelPos.getX() + ";" + pixelPos.getZ() + ")");
		if (ecke1LiegtInPixel) {
			boolean größereFlächeLiegtInnen = !linksKnick ^ uhrzeigersinn;
			ArrayList<Vector2> kantenAnEcke1 = new ArrayList<>();
			kantenAnEcke1.add(kante1.multiply(-1));
			kantenAnEcke1.add(kante2);
			double teilflächeDesPixelsAmPolygon = 0.0;
			boolean ersteKante = true;
			int anzahlDreiecke = 0;
			for (Vector2 kante : kantenAnEcke1) {
				int sgnXSteigung = (int) Math.signum(kante.getX());
				int sgnZSteigung = (int) Math.signum(kante.getZ());
				double deltaX = Math.abs(kante.getX());
				double deltaZ = Math.abs(kante.getZ());
				boolean xIstSchnellereRichtung = deltaX >= deltaZ;
				double steigung;
				if (xIstSchnellereRichtung) {
					steigung = deltaZ / deltaX;
				} else {
					steigung = deltaX / deltaZ;
				}
				double langsamerWertAnPixelWand = steigung * 0.5; //PixelWand liegt bei 0.5 (Mitte im Ursprung und Pixel 1m^2 groß)
				double dreiecksFlächeInQuadrant = 0.5 * langsamerWertAnPixelWand * 0.5;
				boolean xPositiv = sgnXSteigung > 0;
				boolean zPositiv = sgnZSteigung > 0;
				boolean dreieckInQuadrantLinksVonKante = xPositiv ^ zPositiv ^ xIstSchnellereRichtung;
				boolean polygonLinksVonKante = !ersteKante ^ uhrzeigersinn;
				//System.out.println("dreieckInQuadrantLinksVonKante: " + dreieckInQuadrantLinksVonKante + "; polygonLinksVonKante: " + polygonLinksVonKante);
				if (!(dreieckInQuadrantLinksVonKante ^ polygonLinksVonKante)) {
					teilflächeDesPixelsAmPolygon = teilflächeDesPixelsAmPolygon + dreiecksFlächeInQuadrant;
					if (dreiecksFlächeInQuadrant != 0.0) {
						anzahlDreiecke++;
					}
				} else {
					teilflächeDesPixelsAmPolygon = teilflächeDesPixelsAmPolygon - dreiecksFlächeInQuadrant;
					if (dreiecksFlächeInQuadrant != 0.0) {
						anzahlDreiecke--;
					}
				}
				ersteKante = false;
			}
			//Ja, das ist richtig, das der Vektor kante1 hier invertiert wird:
			double anzahlQuadrantenZwischenKanten = 0.5 * (Math.abs(Math.signum(-kante1.getX()) - Math.signum(kante2.getX())) +
					Math.abs(Math.signum(-kante1.getZ()) - Math.signum(kante2.getZ())));
			int anzahlDefaultQuadrantenFürPolygon = -1;
			if (kante1.multiply(-1).dot(kante2) == 0) {
				//Kanten bilden rechten Winkel
				if (größereFlächeLiegtInnen) {
					if (ausgaben) System.out.println("Ecke: true; 90°-Winkel; teilflächeDesPixelsAmPolygon: " + 0.75 + "; Flächenergänzung: " + 0.25 +
							"; pixelPos: (" + pixelPos.getX() + ";" + pixelPos.getZ() + ")");
					return 0.25;
				} else {
					if (ausgaben) System.out.println("Ecke: true; 90°-Winkel; teilflächeDesPixelsAmPolygon: " + 0.25 + "; Flächenergänzung: " + 0.75 +
							"; pixelPos: (" + pixelPos.getX() + ";" + pixelPos.getZ() + ")");
					return 0.75;
				}
			}
			if (kante1.multiply(-1).dot(kante2) == kante1.length() * kante2.length()) {
				//Kanten bilden 180°-Winkel:
				if (ausgaben) System.out.println("Ecke: true; 180°-Winkel; teilflächeDesPixelsAmPolygon: " + 0.5 + "; Flächenergänzung: " + 0.5 +
						"; pixelPos: (" + pixelPos.getX() + ";" + pixelPos.getZ() + ")");
				return 0.5;
			}
			if (kante1.multiply(-1).dot(kante2) == - kante1.length() * kante2.length()) {
				//Kanten bilden 0°-Winkel (Hierbei wird nichts am Polygon ergänzt):
				if (größereFlächeLiegtInnen) {
					if (ausgaben) System.out.println("Ecke: true; 0°-Winkel; teilflächeDesPixelsAmPolygon: " + 0.0 + "; Flächenergänzung: " + 0.0 +
							"; pixelPos: (" + pixelPos.getX() + ";" + pixelPos.getZ() + ")");
					return 0.0;
				} else {
					if (ausgaben) System.out.println("Ecke: true; 0°-Winkel; teilflächeDesPixelsAmPolygon: " + 1.0 + "; Flächenergänzung: " + 0.0 +
							"; pixelPos: (" + pixelPos.getX() + ";" + pixelPos.getZ() + ")");
					return 0.0;
				}
			}
			if (anzahlQuadrantenZwischenKanten < 1) {
				if (anzahlDreiecke < 0) {
					if (größereFlächeLiegtInnen) {
						//Ecke Typ 1:
						anzahlDefaultQuadrantenFürPolygon = 4;
						if (ausgaben) System.out.println("Ecke Typ 1");
					} else {
						//Ecke Typ 2:
						anzahlDefaultQuadrantenFürPolygon = 1;
						if (ausgaben) System.out.println("Ecke Typ 2");
					}
				} else if (anzahlDreiecke == 0) {
					if (größereFlächeLiegtInnen) {
						//Ecke Typ 3:
						anzahlDefaultQuadrantenFürPolygon = 4;
						if (ausgaben) System.out.println("Ecke Typ 3");
					} else {
						//Ecke Typ 4:
						anzahlDefaultQuadrantenFürPolygon = 0;
						if (ausgaben) System.out.println("Ecke Typ 4");
					}
				} else if (anzahlDreiecke > 0) {
					if (größereFlächeLiegtInnen) {
						//Ecke Typ 5:
						anzahlDefaultQuadrantenFürPolygon = 3;
						if (ausgaben) System.out.println("Ecke Typ 5");
					} else {
						//Ecke Typ 6:
						anzahlDefaultQuadrantenFürPolygon = 0;
						if (ausgaben) System.out.println("Ecke Typ 6");
					}
				}
			} else if (anzahlQuadrantenZwischenKanten == 1) {
				if (anzahlDreiecke < 0) {
					if (größereFlächeLiegtInnen) {
						//Ecke Typ 7:
						anzahlDefaultQuadrantenFürPolygon = 4;
						if (ausgaben) System.out.println("Ecke Typ 4");
					} else {
						//Ecke Typ 8:
						anzahlDefaultQuadrantenFürPolygon = 2;
						if (ausgaben) System.out.println("Ecke Typ 8");
					}
				} else if (anzahlDreiecke == 0) {
					if (größereFlächeLiegtInnen) {
						//Ecke Typ 9:
						anzahlDefaultQuadrantenFürPolygon = 3;
						if (ausgaben) System.out.println("Ecke Typ 9");
					} else {
						//Ecke Typ 10:
						anzahlDefaultQuadrantenFürPolygon = 1;
						if (ausgaben) System.out.println("Ecke Typ 10");
					}
				} else if (anzahlDreiecke > 0) {
					if (größereFlächeLiegtInnen) {
						//Ecke Typ 11:
						anzahlDefaultQuadrantenFürPolygon = 2;
						if (ausgaben) System.out.println("Ecke Typ 11");
					} else {
						//Ecke Typ 12:
						anzahlDefaultQuadrantenFürPolygon = 0;
						if (ausgaben) System.out.println("Ecke Typ 12");
					}
				}
			} else {
				if (anzahlDreiecke < 0) {
					if (größereFlächeLiegtInnen) {
						//Ecke Typ 13:
						anzahlDefaultQuadrantenFürPolygon = 3;
						if (ausgaben) System.out.println("Ecke Typ 13");
					} else {
						//Ecke Typ 14:
						anzahlDefaultQuadrantenFürPolygon = 2;
						if (ausgaben) System.out.println("Ecke Typ 14");
					}
				} else if (anzahlDreiecke == 0) {
					if (größereFlächeLiegtInnen) {
						//Ecke Typ 15:
						anzahlDefaultQuadrantenFürPolygon = 2;
						if (ausgaben) System.out.println("Ecke Typ 15");
					} else {
						//Ecke Typ 16:
						anzahlDefaultQuadrantenFürPolygon = 2;
						if (ausgaben) System.out.println("Ecke Typ 16");
					}
				} else if (anzahlDreiecke > 0) {
					if (größereFlächeLiegtInnen) {
						//Ecke Typ 17:
						anzahlDefaultQuadrantenFürPolygon = 2;
						if (ausgaben) System.out.println("Ecke Typ 17");
					} else {
						//Ecke Typ 18:
						anzahlDefaultQuadrantenFürPolygon = 1;
						if (ausgaben) System.out.println("Ecke Typ 18");
					}
				}
			}
			teilflächeDesPixelsAmPolygon = teilflächeDesPixelsAmPolygon + 0.25 * anzahlDefaultQuadrantenFürPolygon;
			if (ausgaben) System.out.println("Ecke: true; teilflächeDesPixelsAmPolygon: " + teilflächeDesPixelsAmPolygon + "; Flächenergänzung: " + (1.0 - teilflächeDesPixelsAmPolygon) +
					"; pixelPos: (" + pixelPos.getX() + ";" + pixelPos.getZ() + ")");
			return 1.0 - teilflächeDesPixelsAmPolygon;
		} else {
			double deltaX = kante1.getX();
			double deltaZ = kante1.getZ();
			if (deltaX == 0.0 || deltaZ == 0.0) {
				//Kante ist senkrecht zum Koordinatensystem
				//Da Kante in Mittelpunkt eines Pixels beginnt, liegt Kante auch hier im Mittelpunkt und beide Teilflächen sind 0.5
				if (ausgaben) System.out.println("Ecke: false; teilflächeDesPixelsAmPolygon: " + 0.5 + "; Flächenergänzung: " + 0.5 +
						"; pixelPos: (" + pixelPos.getX() + ";" + pixelPos.getZ() + ")");
				return 0.5;
			}
			//z=m*x+b <==> b=z-m*x
			double m = deltaZ / deltaX;
			double b = ecke0.getZ() - m * ecke0.getX();
			double pixelRandX1 = pixelPos.getX();
			double pixelRandX2 = pixelPos.getX() + 1;
			double pixelRandZ1 = pixelPos.getZ();
			double pixelRandZ2 = pixelPos.getZ() + 1;
			//System.out.println("pixelRandX1: " + pixelRandX1 + "; pixelRandX2: " + pixelRandX2  + "; pixelRandZ1: " + pixelRandZ1 + "; pixelRandZ2: " + pixelRandZ2);
			double zVonX1 = m * pixelRandX1 + b;
			double zVonX2 = m * pixelRandX2 + b;
			double xVonZ1 = (pixelRandZ1 - b) / m;
			double xVonZ2 = (pixelRandZ2 - b) / m;
			//System.out.println("zVonX1: " + zVonX1 + "; zVonX2: " + zVonX2  + "; xVonZ1: " + xVonZ1 + "; xVonZ2: " + xVonZ2);
			boolean pixelGeschnittenBeiX1 = zVonX1 <= pixelRandZ2 && zVonX1 >= pixelRandZ1; //0.0 <= 3.0 && 0.0 >= 2.0
			boolean pixelGeschnittenBeiX2 = zVonX2 <= pixelRandZ2 && zVonX2 >= pixelRandZ1;
			boolean pixelGeschnittenBeiZ1 = xVonZ1 <= pixelRandX2 && xVonZ1 >= pixelRandX1;
			boolean pixelGeschnittenBeiZ2 = xVonZ2 <= pixelRandX2 && xVonZ2 >= pixelRandX1;

			if (ausgaben) System.out.println("Pixel geschnitten bei: x1: " + pixelGeschnittenBeiX1 +"; x2: " + pixelGeschnittenBeiX2 +
					"; z1: " + pixelGeschnittenBeiZ1 + "; z2: " + pixelGeschnittenBeiZ2 + "; m: " + m + "; b: " + b);
			double teilflächeDesPixelsAmPolygon;
			if ((pixelGeschnittenBeiX1 && pixelGeschnittenBeiX2) ||
				(pixelGeschnittenBeiZ1 && pixelGeschnittenBeiZ2)
			) {
				//Pixel unterteilt in zwei Vierecke
				if (pixelGeschnittenBeiX1 && pixelGeschnittenBeiX2) {
					double zVonXMittelwert = 0.5 * (zVonX1 + zVonX2);
					boolean viereckAufPositiverZSeiteInPolygon = linksKnick ^ kante1.getX() > 0;
					if (viereckAufPositiverZSeiteInPolygon) {
						teilflächeDesPixelsAmPolygon = pixelRandZ2 - zVonXMittelwert;
					} else {
						teilflächeDesPixelsAmPolygon = zVonXMittelwert - pixelRandZ1;
					}
				} else {
					double xVonZMittelwert = 0.5 * (xVonZ1 + xVonZ2);
					boolean viereckAufPositiverXSeiteInPolygon = linksKnick ^ kante1.getZ() < 0;
					if (viereckAufPositiverXSeiteInPolygon) {
						teilflächeDesPixelsAmPolygon = pixelRandX2 - xVonZMittelwert;
					} else {
						teilflächeDesPixelsAmPolygon = xVonZMittelwert - pixelRandX1;
					}

				}
			} else {
				//Pixel unterteilt in Dreieck und Fünfeck
				Double dreiecksFläche = null;
				Vector2 pixelEckPunktBeiDreieck = null;
				if (pixelGeschnittenBeiX1 && pixelGeschnittenBeiZ1) {
					pixelEckPunktBeiDreieck = Vector2.at(pixelRandX1, pixelRandZ1);
					dreiecksFläche = 0.5 * (zVonX1 - pixelRandZ1) * (xVonZ1 - pixelRandX1);
				} else if (pixelGeschnittenBeiX1 && pixelGeschnittenBeiZ2) {
					pixelEckPunktBeiDreieck = Vector2.at(pixelRandX1, pixelRandZ2);
					dreiecksFläche = 0.5 * (xVonZ2 - pixelRandX1) * (pixelRandZ2 - zVonX1);
				} else if (pixelGeschnittenBeiX2 && pixelGeschnittenBeiZ1) {
					pixelEckPunktBeiDreieck = Vector2.at(pixelRandX2, pixelRandZ1);
					dreiecksFläche = 0.5 * (pixelRandX2 - xVonZ1) * (zVonX2 - pixelRandZ1);
				} else if (pixelGeschnittenBeiX2 && pixelGeschnittenBeiZ2) {
					pixelEckPunktBeiDreieck = Vector2.at(pixelRandX2, pixelRandZ2);
					dreiecksFläche = 0.5 * (pixelRandZ2 - zVonX2) * (pixelRandX2 - xVonZ2);
				}
				Vector2 rechtsLinksVektor = pixelEckPunktBeiDreieck.subtract(ecke0);
				boolean dreieckLinksVonKante  = kante1.getX() * rechtsLinksVektor.getZ() - kante1.getZ() * rechtsLinksVektor.getX() > 0;
				Double flächeInPixelLinksVonKante = null;
				Double flächeInPixelRechtsVonKante = null;
				if (dreieckLinksVonKante) {
					flächeInPixelLinksVonKante = dreiecksFläche;
					flächeInPixelRechtsVonKante = 1 - dreiecksFläche;
				} else {
					flächeInPixelRechtsVonKante = dreiecksFläche;
					flächeInPixelLinksVonKante = 1 - dreiecksFläche;
				}
				if (linksKnick) {
					teilflächeDesPixelsAmPolygon = flächeInPixelLinksVonKante;
				} else {
					teilflächeDesPixelsAmPolygon = flächeInPixelRechtsVonKante;
				}
			}

			//Wenn Pixel mit einer Kante geschnitten wird:
			//Wenn der Polygonteil größer ist,
			//dann gehört der Mittelpunkt des Pixels und damit auch der Pixel zur Region und der Nicht-Polygonteil muss als Ergänzung zurückgegeben werden,
			//sonst muss der Polygonteil als negativ zurückgegeben werden
			if (teilflächeDesPixelsAmPolygon >= 0.5) {
				if (ausgaben) System.out.println("Ecke: false; teilflächeDesPixelsAmPolygon: " + teilflächeDesPixelsAmPolygon + "; Flächenergänzung: " + (1.0 - teilflächeDesPixelsAmPolygon) +
						"; pixelPos: (" + pixelPos.getX() + ";" + pixelPos.getZ() + ")");
				return 1.0 - teilflächeDesPixelsAmPolygon;
			} else {
				if (ausgaben) System.out.println("Ecke: false; teilflächeDesPixelsAmPolygon: " + teilflächeDesPixelsAmPolygon + "; Flächenergänzung: " + (-teilflächeDesPixelsAmPolygon) +
						"; pixelPos: (" + pixelPos.getX() + ";" + pixelPos.getZ() + ")");
				return -teilflächeDesPixelsAmPolygon;
			}
		}
	}

	public static Boolean verläuftPolygonImUhrzeigersinn(ArrayList<Vector2> eckpunkte) {
		int anzahlLinksKnicke = 0;
		int anzahlRechtsKnicke = 0;
		Vector2 letzterPunkt = eckpunkte.get(eckpunkte.size() - 1);
		for (int i = 0; i < eckpunkte.size(); i++) {
			Vector2 punkt = eckpunkte.get(i);
			Vector2 nächsterPunkt = eckpunkte.get(0);
			if (i < eckpunkte.size() - 1) {
				nächsterPunkt = eckpunkte.get(i+1);
			}
			Vector2 kantenVektorVorPunkt = punkt.subtract(letzterPunkt);
			Vector2 kantenVektorNachPunkt = letzterPunkt.subtract(nächsterPunkt);
			double quasiKreuzProdukt = kantenVektorVorPunkt.getX() * kantenVektorNachPunkt.getZ() - kantenVektorVorPunkt.getZ() * kantenVektorNachPunkt.getX();
			if (quasiKreuzProdukt > 0) {
				//spitzer Winkel ==> Linksknick
				anzahlLinksKnicke++;
			} else if (quasiKreuzProdukt < 0) {
				//stumpfer Winkel ==> Rechtsknick
				anzahlRechtsKnicke++;
			}
			letzterPunkt = punkt;
		}
		if (anzahlLinksKnicke == anzahlRechtsKnicke) {
			//Fehler: Polygon überschneidet sich
			return  null;
		}
		if (anzahlLinksKnicke < anzahlRechtsKnicke) {
			return true;
		}
		return false;
	}

	public static ArrayList<Vector2> punktListeInvertiert (ArrayList<Vector2> punktListe) {
		if (punktListe == null) {
			return null;
		} else if (punktListe.size() <= 1) {
			return punktListe;
		}
		ArrayList<Vector2> punktListeInvertiert = new ArrayList<>();
		for (int i = punktListe.size() - 1; i >= 0; i--) {
			punktListeInvertiert.add(punktListe.get(i));
		}
		return punktListeInvertiert;
	}

	public static Vector2 schnittpunktVonZweiStrecken (Vector2 strecke1punkt1, Vector2 strecke1punkt2, Vector2 strecke2punkt1, Vector2 strecke2punkt2) {
		Vector2 strecke1vector = strecke1punkt2.subtract(strecke1punkt1);
		Vector2 strecke2vector = strecke2punkt2.subtract(strecke2punkt1);
		System.out.println("schnittpunktVonZweiStrecken: ");
		System.out.println("Strecke 1: (" + strecke1punkt1.getX() + "," + strecke1punkt1.getZ() + ") --> (" + strecke1punkt2.getX() + "," + strecke1punkt2.getZ());
		System.out.println("Strecke 2: (" + strecke2punkt1.getX() + "," + strecke2punkt1.getZ() + ") --> (" + strecke2punkt2.getX() + "," + strecke2punkt2.getZ());
		if (Math.abs(strecke1vector.dot(strecke2vector)) == strecke1vector.length() * strecke2vector.length()) {
			System.out.println("(Strecken sind parallel)");
			//(Strecken sind parallel)
			if (((strecke1punkt1.getX() <= strecke2punkt1.getX() && strecke2punkt1.getX() <= strecke1punkt2.getX()) ||
					(strecke1punkt1.getX() >= strecke2punkt1.getX() && strecke2punkt1.getX() >= strecke1punkt2.getX()))
				&&
				((strecke1punkt1.getZ() <= strecke2punkt1.getZ() && strecke2punkt1.getZ() <= strecke1punkt2.getZ()) ||
					(strecke1punkt1.getZ() >= strecke2punkt1.getZ() && strecke2punkt1.getZ() >= strecke1punkt2.getZ()))
			) {
				//Strecken haben unendlich viele Schnittpunkte oder genau einen und bilden eine gesamte Strecke:
				//Gib ersten Punkt von Strecke2 auf Strecke1 zurück
				if (strecke1punkt1.distance(strecke2punkt1) < strecke1punkt1.distance(strecke2punkt2)) {
					System.out.println("Strecken haben unendlich viele Schnittpunkte oder genau einen und bilden eine gesamte Strecke; " +
							"ersterSchnittpunkt: (" + strecke2punkt1.getX() + "," + strecke2punkt1.getZ() + ")");
					return strecke2punkt1;
				} else {
					System.out.println("Strecken haben unendlich viele Schnittpunkte oder genau einen und bilden eine gesamte Strecke; " +
							"ersterSchnittpunkt: (" + strecke2punkt2.getX() + "," + strecke2punkt2.getZ() + ")");
					return strecke2punkt2;
				}
			} else {
				System.out.println("Strecken haben 0 Schnittpunkte");
				//Strecken haben 0 Schnittpunkte
				return null;
			}
		}
		double m1 = strecke1vector.getZ() / strecke1vector.getX();
		double m2 = strecke2vector.getZ() / strecke2vector.getX();
		//b=z-m*x
		double b1 = strecke1punkt1.getZ() - m1 * strecke1punkt1.getX();
		double b2 = strecke2punkt1.getZ() - m2 * strecke2punkt1.getX();
		Double x = null;
		Double z = null;
		if (m1 != Double.POSITIVE_INFINITY && m1 != Double.NEGATIVE_INFINITY && m2 != Double.POSITIVE_INFINITY && m2 != Double.NEGATIVE_INFINITY) {
			//m1*x+b1=m2*x+b2 ==> x=(b2-b1)/(m1-m2)
			x = (b2 - b1) / (m1 - m2);
			z = m1 * x + b1;
		} else if ((m1 == Double.POSITIVE_INFINITY || m1 == Double.NEGATIVE_INFINITY) && m2 != Double.POSITIVE_INFINITY && m2 != Double.NEGATIVE_INFINITY) {
			x = strecke1punkt1.getX();
			z = m2 * x + b2;
		} else if (m1 != Double.POSITIVE_INFINITY && m1 != Double.NEGATIVE_INFINITY && (m2 == Double.POSITIVE_INFINITY || m2 == Double.NEGATIVE_INFINITY)) {
			x = strecke2punkt1.getX();
			z = m1 * x + b1;
		} else {
			System.out.println("ERROR: ClaimUtils.schnittpunktVonZweiStrecken: Die Steigungen der beiden Strecken sind unendlich, aber die Strecken sind auch nicht parallel.");
		}
		System.out.println("m1: " + m1 + "; m2: " + m2 + "; b1: " + b1 + "; b2: " + b2 + "; x: " + x + "; z: " + z);
		//Liegt der Schnittpunkt der zwei Geraden, die aus den zwei Strecken weitergeführt werden können, auf beiden Strecken?
		if (((strecke1punkt1.getX() <= x && x <= strecke1punkt2.getX()) || (strecke1punkt1.getX() >= x && x >= strecke1punkt2.getX())) &&
			((strecke1punkt1.getZ() <= z && z <= strecke1punkt2.getZ()) || (strecke1punkt1.getZ() >= z && z >= strecke1punkt2.getZ())) &&
			((strecke2punkt1.getX() <= x && x <= strecke2punkt2.getX()) || (strecke2punkt1.getX() >= x && x >= strecke2punkt2.getX())) &&
			((strecke2punkt1.getZ() <= z && z <= strecke2punkt2.getZ()) || (strecke2punkt1.getZ() >= z && z >= strecke2punkt2.getZ()))
		) {
			//Strecken schneiden oder berühren sich
			System.out.println("Strecken schneiden oder berühren sich");
			return Vector2.at(x,z);
		} else {
			System.out.println("Strecken haben 0 Schnittpunkte");
			//Strecken haben 0 Schnittpunkte
			return null;
		}
	}

	public static boolean liegtPunktCAufStreckeAB(Vector2 C, Vector2 A, Vector2 B) {
		Vector2 AC = C.subtract(A);
		Vector2 BC = C.subtract(B);
		if (round(Math.abs(AC.dot(BC)) - AC.length() * BC.length(),8) == 0) { //hier muss gerundet werden, da sonst noch feinere Rundungsfehler für ein falsches Ergebnis sorgen können
			//AC und BC sind parallel
			if (((A.getX() <= C.getX() && C.getX() <= B.getX()) || (A.getX() >= C.getX() && C.getX() >= B.getX())) &&
					((A.getZ() <= C.getZ() && C.getZ() <= B.getZ()) || (A.getZ() >= C.getZ() && C.getZ() >= B.getZ()))
			) {
				// C liegt zwischen A und B
				return true;
			}
		}
		return false;
	}

	public static Boolean liegtPunktInPolygon (Vector2 testpunkt, ArrayList<Vector2> polygon) {
		//Punkt-in-Polygon-Test nach Jordan
		if (polygon.contains(testpunkt)) {return true; }
		Vector2 parallelZuKeinerWorldEditKante = Vector2.at(2,Math.PI);
		double deltaXMax = 0;
		double deltaZMax = 0;
		for (Vector2 eckpunkt : polygon) {
			if (Math.abs(testpunkt.getX() - eckpunkt.getX()) > deltaXMax) {
				deltaXMax = Math.abs(testpunkt.getX() - eckpunkt.getX());
			}
			if (Math.abs(testpunkt.getZ() - eckpunkt.getZ()) > deltaZMax) {
				deltaZMax = Math.abs(testpunkt.getZ() - eckpunkt.getZ());
			}
		}
		parallelZuKeinerWorldEditKante = parallelZuKeinerWorldEditKante.multiply(Vector2.at(deltaXMax,deltaZMax));
		Vector2 fernerPunkt = testpunkt.add(parallelZuKeinerWorldEditKante);
		Vector2 letzterEckpunkt = polygon.get(polygon.size() - 1);
		int anzahlSchnitte = 0;
		for (Vector2 eckpunkt : polygon) {
			if (schnittpunktVonZweiStrecken(letzterEckpunkt, eckpunkt, testpunkt, fernerPunkt) != null) {
				anzahlSchnitte ++;
			}
			letzterEckpunkt = eckpunkt;
		}
		if (anzahlSchnitte % 2 == 0) {
			return false;
		} else {
			return true;
		}
	}

	public static Vector2 nächsterEckpunkt (Vector2 punktAufPolygonKante, ArrayList<Vector2> polygon) {
		//Zuerst überprüfe, ob punktAufPolygonKante ein Eckpunkt ist und gebe wenn ja, den nächsten Eckpunkt zurück
		for (int i = 0; i < polygon.size(); i++) {
			Vector2 ecke = polygon.get(i);
			if (ecke.equals(punktAufPolygonKante)) {
				if (i == polygon.size() - 1) {
					return polygon.get(0);
				} else {
					return polygon.get(i+1);
				}
			}
		}
		//punktAufPolygonKante ist kein Eckpunkt:
		for (int i = 0; i < polygon.size(); i++) {
			Vector2 möglicheVorherigeEcke = polygon.get(i);
			Vector2 möglicheNächsteEcke;
			if (i == polygon.size() - 1) {
				möglicheNächsteEcke = polygon.get(0);
			} else {
				möglicheNächsteEcke = polygon.get(i+1);
			}
			//Liegt punktAufPolygonKante auf der Strecke von möglicheVorherigeEcke zu möglicheNächsteEcke?:
			if (liegtPunktCAufStreckeAB(punktAufPolygonKante, möglicheNächsteEcke, möglicheVorherigeEcke)) {
				return möglicheNächsteEcke;
			}
		}
		return null;
	}

	public static Vector2 vorherigerEckpunkt (Vector2 punktAufPolygonKante, ArrayList<Vector2> polygon) {
		//Zuerst überprüfe, ob punktAufPolygonKante ein Eckpunkt ist und gebe wenn ja, den vorherigen Eckpunkt zurück
		for (int i = 0; i < polygon.size(); i++) {
			Vector2 ecke = polygon.get(i);
			if (ecke.equals(punktAufPolygonKante)) {
				if (i == 0) {
					return polygon.get(polygon.size() - 1);
				} else {
					return polygon.get(i-1);
				}
			}
		}
		//punktAufPolygonKante ist kein Eckpunkt:
		for (int i = 0; i < polygon.size(); i++) {
			Vector2 möglicheVorherigeEcke = polygon.get(i);
			Vector2 möglicheNächsteEcke;
			if (i == polygon.size() - 1) {
				möglicheNächsteEcke = polygon.get(0);
			} else {
				möglicheNächsteEcke = polygon.get(i+1);
			}
			//Liegt punktAufPolygonKante auf der Strecke von möglicheVorherigeEcke zu möglicheNächsteEcke?:
			if (liegtPunktCAufStreckeAB(punktAufPolygonKante, möglicheNächsteEcke, möglicheVorherigeEcke)) {
				return möglicheVorherigeEcke;
			}
		}
		return null;
	}

	public static BlockVector2 nächsterEckpunkt (BlockVector2 eckpunkt, List<BlockVector2> polygon) {
		for (int i = 0; i < polygon.size(); i++) {
			BlockVector2 ecke = polygon.get(i);
			if (ecke.equals(eckpunkt)) {
				if (i == polygon.size() - 1) {
					return polygon.get(0);
				} else {
					return polygon.get(i+1);
				}
			}
		}
		return null;
	}

	public static IntersectionResult ersterSchnittpunktMitPolygonAufStrecke (Vector2 startpunkt, Vector2 zielpunkt, ArrayList<Vector2> polygon, boolean schnittPunktDarfNichtStartpunktSein) {
		Vector2 ersterSchnittpunkt = null;
		Vector2 eckpunkt1VonSchnittpunktKante = null;
		Vector2 eckpunkt2VonSchnittpunktKante = null;
		for (Vector2 eckpunkt : polygon) {
			Vector2 nächsterEckpunkt = nächsterEckpunkt(eckpunkt, polygon);
			Vector2 schnittpunkt = schnittpunktVonZweiStrecken(startpunkt, zielpunkt, eckpunkt, nächsterEckpunkt);
			if (schnittpunkt != null) {
				if (!schnittPunktDarfNichtStartpunktSein || !schnittpunkt.equals(startpunkt)) {
					if (ersterSchnittpunkt == null) {
						ersterSchnittpunkt = schnittpunkt;
						eckpunkt1VonSchnittpunktKante = eckpunkt;
						eckpunkt2VonSchnittpunktKante = nächsterEckpunkt;
					} else {
						if (startpunkt.distance(schnittpunkt) <= startpunkt.distance(ersterSchnittpunkt) && !schnittpunkt.equals(startpunkt)) {
							if (startpunkt.distance(schnittpunkt) == startpunkt.distance(ersterSchnittpunkt)) {
								//Wenn bei zwei Kanten vom Polygon der Schnittpunkt mit der Kante der selbe ist,
								//soll die Kante ausgewählt werden, die weiter rechts führt (Da beim Vereinen von 2 Polygons Richtung gegen den Uhrzeigersinn gewählt wurde)
								if (eckpunkt2VonSchnittpunktKante.equals(schnittpunkt)) {
									//Wenn der Eckpunkt von kante1 der Schnittpunkt ist, ist die Richtung somit schon vorgegeben und der Schnittpunkt soll sich auf kante2 beziehen
									ersterSchnittpunkt = schnittpunkt;
									eckpunkt1VonSchnittpunktKante = eckpunkt;
									eckpunkt2VonSchnittpunktKante = nächsterEckpunkt;
								} else if (nächsterEckpunkt.equals(schnittpunkt)) {
									//Wenn der Eckpunkt von kante2 der Schnittpunkt ist, ist die Richtung somit schon vorgegeben und der Schnittpunkt soll sich auf kante1 beziehen
									//nichts tun, da Kante1 schon ausgewählt
								} else {
									System.out.println("ersterSchnittpunktMitPolygonAufStrecke: Dieser Fall sollte eigentlich nicht eintreten. " +
											"Bitte vervollständigen, jetzt wo er scheinbar doch eintritt.");
									Vector2 kante1 = eckpunkt2VonSchnittpunktKante.subtract(schnittpunkt);
									Vector2 kante2 = nächsterEckpunkt.subtract(schnittpunkt);
								}
							} else {
								ersterSchnittpunkt = schnittpunkt;
								eckpunkt1VonSchnittpunktKante = eckpunkt;
								eckpunkt2VonSchnittpunktKante = nächsterEckpunkt;
							}
						}
					}
				}
			}
		}
		if (ersterSchnittpunkt == null) {
			return null;
		}
		IntersectionResult result = new IntersectionResult(ersterSchnittpunkt, eckpunkt1VonSchnittpunktKante, eckpunkt2VonSchnittpunktKante);
		return result;
	}

	public static List<BlockVector2> eckpunkteGanzAusEckpunkteExakt (ArrayList<Vector2> eckpunkteExakt) {
		ArrayList<BlockVector2> list = new ArrayList<>();
		if (eckpunkteExakt == null) {
			return null;
		}
		for (Vector2 eckpunktExakt : eckpunkteExakt) {
			list.add(BlockVector2.at((int)(eckpunktExakt.getX() - 0.5),(int)(eckpunktExakt.getZ() - 0.5)));
		}
		List<BlockVector2> targetList = new ArrayList<>(list.size());
		targetList.addAll(list);
		return targetList;
	}

	public static ArrayList<Vector2> eckpunkteExaktAusEckpunkteGanz (List<BlockVector2> eckpunkteGanz) {
		ArrayList<Vector2> list = new ArrayList<>();
		if (eckpunkteGanz == null) {
			return null;
		}
		for (BlockVector2 eckpunktExakt : eckpunkteGanz) {
			list.add(Vector2.at(eckpunktExakt.getBlockX() + 0.5, eckpunktExakt.getBlockZ() + 0.5));
		}
		return list;
	}

	public static List<BlockVector2> polygonAusKuboidRegion (CuboidRegion cuboidRegion) {
		List<BlockVector2> eckpunkte = new ArrayList<>(4);
		eckpunkte.add(BlockVector2.at(cuboidRegion.getMinimumPoint().getX(),cuboidRegion.getMinimumPoint().getZ()));
		eckpunkte.add(BlockVector2.at(cuboidRegion.getMinimumPoint().getX(),cuboidRegion.getMaximumPoint().getZ()));
		eckpunkte.add(BlockVector2.at(cuboidRegion.getMaximumPoint().getX(),cuboidRegion.getMaximumPoint().getZ()));
		eckpunkte.add(BlockVector2.at(cuboidRegion.getMaximumPoint().getX(),cuboidRegion.getMinimumPoint().getZ()));
		return eckpunkte;
	}

	public static List<BlockVector2> polygonAusKuboidRegion (ProtectedCuboidRegion cuboidRegion) {
		List<BlockVector2> eckpunkte = new ArrayList<>(4);
		eckpunkte.add(BlockVector2.at(cuboidRegion.getMinimumPoint().getX(),cuboidRegion.getMinimumPoint().getZ()));
		eckpunkte.add(BlockVector2.at(cuboidRegion.getMinimumPoint().getX(),cuboidRegion.getMaximumPoint().getZ()));
		eckpunkte.add(BlockVector2.at(cuboidRegion.getMaximumPoint().getX(),cuboidRegion.getMaximumPoint().getZ()));
		eckpunkte.add(BlockVector2.at(cuboidRegion.getMaximumPoint().getX(),cuboidRegion.getMinimumPoint().getZ()));
		return eckpunkte;
	}

	public static boolean polygonHatEckpunkteMehrfach (List<BlockVector2> eckpunkte) {
		if (eckpunkte == null) {
			return false;
		}
 		List<BlockVector2> eckpunkteClon = new ArrayList<>();
		for (BlockVector2 punkt : eckpunkte) {
			eckpunkteClon.add(punkt);
		}
		int anzahlPunkte = eckpunkte.size();
		for (BlockVector2 punkt : eckpunkte) {
			eckpunkteClon.remove(punkt);
			if (eckpunkteClon.contains(punkt)) {
				return true;
			}
		}
		return false;
	}

	public static List<BlockVector2> polygonOhneRedundantePunkte (List<BlockVector2> eckpunkte) {
		if (eckpunkte == null) {
			return null;
		}
		BlockVector2 letzterPunkt = eckpunkte.get(eckpunkte.size() - 1);
		List<BlockVector2> polygonOhneRedundantePunkte = new ArrayList<>();
		for	(BlockVector2 punkt : eckpunkte) {
			BlockVector2 nächsterPunkt = nächsterEckpunkt(punkt,eckpunkte);
			if (!punkt.equals(letzterPunkt)) {
				BlockVector2 kante1 = punkt.subtract(letzterPunkt);
				BlockVector2 kante2 = nächsterPunkt.subtract(punkt);
				//Sind kante1 und kante2 nicht parallel?
				if ((Math.abs(kante1.dot(kante2)) != kante1.length() * kante2.length()) || kante2.length() == 0.0) {
					polygonOhneRedundantePunkte.add(punkt);
				} else {
					Bukkit.broadcastMessage("Punkt ist redundant (liegt auf Strecke der Nachbarpunkte." +
							"punkt: (" + punkt.getX() + "," + punkt.getZ() + "); " +
							"letzterPunkt: (" + letzterPunkt.getX() + "," + letzterPunkt.getZ() + "); " +
							"nächsterPunkt: (" + nächsterPunkt.getX() + "," + nächsterPunkt.getZ() + "); " +
							"kante1: (" + kante1.getX() + "," + kante1.getZ() + "); " +
							"kante2: (" + kante2.getX() + "," + kante2.getZ() + "); ");
				}
			}
			letzterPunkt = punkt;
		}
		return polygonOhneRedundantePunkte;
	}

	public static boolean sindPolygoneGleich(List<BlockVector2> polygon1, List<BlockVector2> polygon2) {
		List<BlockVector2> symmetrischeDifferenzDerPolygonPunkteMengen = new ArrayList<>();
		for (BlockVector2 punkt : polygon1) {
			if (!polygon2.contains(punkt)) {
				symmetrischeDifferenzDerPolygonPunkteMengen.add(punkt);
			}
		}
		for (BlockVector2 punkt : polygon2) {
			if (!polygon1.contains(punkt)) {
				symmetrischeDifferenzDerPolygonPunkteMengen.add(punkt);
			}
		}
		if (symmetrischeDifferenzDerPolygonPunkteMengen.size() == 0) {
			return true;
		}
		return false;
	}

	public static Polygonal2DRegion markierungAlsPolygon(ProtectedRegion oldRegion, Region markierung) {
		Polygonal2DRegion markierungAlsPolygon;
		if (markierung instanceof Polygonal2DRegion) {
			markierungAlsPolygon = ((Polygonal2DRegion) markierung).clone();
			markierungAlsPolygon.setMinimumY(oldRegion.getMinimumPoint().getY());
			markierungAlsPolygon.setMaximumY(oldRegion.getMaximumPoint().getY());
		} else {
			markierungAlsPolygon = new Polygonal2DRegion(
					markierung.getWorld(), ClaimUtils.polygonAusKuboidRegion((CuboidRegion) markierung),
					oldRegion.getMinimumPoint().getY(), oldRegion.getMaximumPoint().getY());
		}
		return markierungAlsPolygon;
	}

	public static double round(double value, int decimalPoints) {
		double d = Math.pow(10, decimalPoints);
		return Math.round(value * d) / d;
	}
}

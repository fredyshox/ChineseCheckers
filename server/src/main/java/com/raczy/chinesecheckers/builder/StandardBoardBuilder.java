package com.raczy.chinesecheckers.builder;

import com.raczy.chinesecheckers.Board;
import com.raczy.chinesecheckers.Field;
import com.raczy.chinesecheckers.builder.BoardBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Standard implementation of BoardBuilder interface
 * Created by kacperraczy on 11.12.2017.
 */
public class StandardBoardBuilder extends BoardBuilder {
    private Integer[][] mainBoardEdgeVertex = { {0, 1, 2, 3, 4},
                                                {4, 10, 17, 25, 34},
                                                {34, 42, 49, 55, 60},
                                                {60, 59, 58, 57, 56},
                                                {56, 50, 43, 35, 26},
                                                {26, 18, 11, 5, 0} };
    private Map<Integer, Field> mainFieldMap;
    private Map<Integer, Map<Integer, Field>> playerZonesMap = new HashMap<>();


    @Override
    public void generateMainBoardPart() {
        int niter = 5;
        int i = 0;
        Map<Integer, Field> fieldMap = new HashMap<Integer, Field>();
        Field current;
        Field temp;
        while (niter <= 9) {
            for(i = 0; i < niter; i++) {
                current = new Field(gen.generate());

                if (i == 0) {
                    //first
                    temp = fieldMap.get(current.getId() - niter + 1);
                    if (temp != null) {
                        connectNodes(Field.TOPRIGHT, current, temp);
                    }
                } else {
                    temp = fieldMap.get(current.getId() - niter);
                    if(temp != null) {
                        connectNodes(Field.TOPLEFT, current, temp);
                    }

                    temp = fieldMap.get(current.getId() - 1);
                    connectNodes(Field.LEFT, current, temp);

                    if (i != (niter - 1)) {
                        temp = fieldMap.get(current.getId() - niter + 1);
                        if (temp != null) {
                            connectNodes(Field.TOPRIGHT, current, temp);
                        }
                    }
                }

                fieldMap.put(current.getId(), current);
            }

            niter += 1;
        }

        niter -= 2;
        while(niter >= 5) {
            for (i = 0; i<niter; i++) {
                current = new Field(gen.generate());

                temp = fieldMap.get(current.getId() - niter - 1);
                connectNodes(Field.TOPLEFT, current, temp);

                temp = fieldMap.get(current.getId() - niter);
                connectNodes(Field.TOPRIGHT, current, temp);

                if (i != 0) {
                    temp = fieldMap.get(current.getId() - 1);
                    connectNodes(Field.LEFT, current, temp);
                }

                fieldMap.put(current.getId(), current);
            }
            niter -= 1;
        }

        this.mainFieldMap = fieldMap;

    }


    @Override
    public void generatePlayerBoardPart(int side) {
        if (playerZonesMap.size() > 6) {
            logger.error("playerZones", "Cannot generate another player zone. It achieved its max capacity");
            return;
        }else if(side > 5) {
            logger.error("playerZones", "side with index: " + side + " doesn't exist");
            return;
        }

        Integer[] edgeVertex = mainBoardEdgeVertex[side];
        Edges edges = getEdges(side);
        Field[] fields = new Field[edgeVertex.length];
        Field temp;

        for(int i = 0; i<edgeVertex.length; i++) {
            temp = this.mainFieldMap.get(edgeVertex[i]);
            if(temp != null) {
                fields[i] = temp;
            }else {
                logger.error("generatePlayerGraphs","Main Board Graph not generated properly.");
                return;
            }
        }

        //specifying edge specific hook sides

        Map<Integer, Field> playerFields = generatePlayerGraph(fields, edges);
        this.playerZonesMap.put(side, playerFields);
        this.mainFieldMap.putAll(playerFields);
    }

    @Override
    public Board getResult() {
        Board board = new Board(this.playerZonesMap.size());
        board.setFieldMap(this.mainFieldMap);
        board.setPlayerZones(this.playerZonesMap);

        return board;
    }


    private Map<Integer, Field> generatePlayerGraph(Field[] edgeVertex, Edges edges) {
        if (edgeVertex.length != 5) {
            logger.error("generatePlayerGraph","Error while creating player graph");
        }

        Map<Integer, Field> result = new HashMap<Integer, Field>();
        int niter = 4;
        Field current;
        Field temp;

        while(niter > 0) {

            for(int i = 0; i < niter; i++) {
                current = new Field(gen.generate());

                if (i != 0) {
                    temp = result.get(current.getId() - 1);
                    connectNodes(edges.connector, current, temp);
                }

                if (niter == 4) {
                    temp = edgeVertex[i];
                    connectNodes(edges.edge1, temp, current);

                    temp = edgeVertex[i+1];
                    connectNodes(edges.edge2, temp, current);
                } else {
                    temp = result.get(current.getId() - niter - 1);
                    //opposite topright
                    connectNodes( Field.oppositeEdge(edges.edge1), current, temp);

                    temp = result.get(current.getId() - niter);
                    //opposite topleft
                    connectNodes( Field.oppositeEdge(edges.edge2), current, temp);
                }

                result.put(current.getId(), current);
            }

            niter -= 1;
        }

        return result;
    }

    private class Edges {
        int connector;
        int edge1;
        int edge2;
    }

    private Edges getEdges(int side) {
        Edges result = new Edges();
        switch (side) {
            case 0:
                result.connector = Field.LEFT;
                result.edge1 = Field.TOPRIGHT;
                result.edge2 = Field.TOPLEFT;
                break;
            case 1:
                result.connector = Field.TOPLEFT;
                result.edge1 = Field.RIGHT;
                result.edge2 = Field.TOPRIGHT;
                break;
            case 2:
                result.connector = Field.TOPRIGHT;
                result.edge1 = Field.BOTTOMRIGHT;
                result.edge2 = Field.RIGHT;
                break;
            case 3:
                result.connector = Field.RIGHT;
                result.edge1 = Field.BOTTOMLEFT;
                result.edge2 = Field.BOTTOMRIGHT;
                break;
            case 4:
                result.connector = Field.BOTTOMRIGHT;
                result.edge1 = Field.LEFT;
                result.edge2 = Field.BOTTOMLEFT;
                break;
            case 5:
                result.connector = Field.BOTTOMLEFT;
                result.edge1 = Field.TOPLEFT;
                result.edge2 = Field.LEFT;
                break;
            default:
                logger.error("getEdges", "provided edge number doesn't exist");
                break;
        }

        return result;
    }

    private void connectNodes(int eno, Field f1, Field f2) {
        int opposite = Field.oppositeEdge(eno);
        f1.getNeighbours()[eno] = f2;
        f2.getNeighbours()[opposite] = f1;
    }
}

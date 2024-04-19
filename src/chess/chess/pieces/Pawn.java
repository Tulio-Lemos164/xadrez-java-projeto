package chess.chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

    private ChessMatch chessMatch;

    public Pawn(Board board, Color color, ChessMatch chessMatch) {
        super(board, color);
        this.chessMatch = chessMatch;
    }

    @Override
    public String toString(){
        return "P";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

        Position p = new Position(0, 0);

        if (getColor() == Color.WHITE){
            //Movimento normal do Peão
            p.setValues(position.getRow() - 1, position.getColumn());
            if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
                mat[p.getRow()][p.getColumn()] = true;
            }

            //Movimento inicial do Peão
            p.setValues(position.getRow() - 2, position.getColumn());
            Position p2 = new Position(position.getRow() - 1, position.getColumn());
            if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && getMoveCount()==0 && !getBoard().thereIsAPiece(p2)){
                mat[p.getRow()][p.getColumn()] = true;
            }

            //Movimento do Peão quando tem inimigo à nordeste
            p.setValues(position.getRow() - 1, position.getColumn()+1);
            if (getBoard().positionExists(p) && isThereOpponentPiece(p)){
                mat[p.getRow()][p.getColumn()] = true;
            }

            //Movimento do Peão quando tem inimigo à noroeste
            p.setValues(position.getRow() - 1, position.getColumn()-1);
            if (getBoard().positionExists(p) && isThereOpponentPiece(p)){
                mat[p.getRow()][p.getColumn()] = true;
            }

            //Movimento especial en passant
            if (position.getRow()== 3){
                Position esquerda = new Position(position.getRow(), position.getColumn() - 1);
                if (getBoard().positionExists(esquerda) && isThereOpponentPiece(esquerda) && getBoard().piece(esquerda) == chessMatch.getEnPassantVulnerable()){
                    mat[esquerda.getRow() - 1][esquerda.getColumn()] = true;
                }
                Position direita = new Position(position.getRow(), position.getColumn() + 1);
                if (getBoard().positionExists(direita) && isThereOpponentPiece(direita) && getBoard().piece(direita) == chessMatch.getEnPassantVulnerable()){
                    mat[direita.getRow() - 1][direita.getColumn()] = true;
                }
            }
        }
        else {
            //Movimento normal do Peão
            p.setValues(position.getRow() + 1, position.getColumn());
            if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
                mat[p.getRow()][p.getColumn()] = true;
            }

            //Movimento inicial do Peão
            p.setValues(position.getRow() + 2, position.getColumn());
            Position p2 = new Position(position.getRow() + 1, position.getColumn());
            if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && getMoveCount()==0 && !getBoard().thereIsAPiece(p2)){
                mat[p.getRow()][p.getColumn()] = true;
            }

            //Movimento do Peão quando tem inimigo à sudeste
            p.setValues(position.getRow() + 1, position.getColumn()+1);
            if (getBoard().positionExists(p) && isThereOpponentPiece(p)){
                mat[p.getRow()][p.getColumn()] = true;
            }

            //Movimento do Peão quando tem inimigo à sudoeste
            p.setValues(position.getRow() + 1, position.getColumn()-1);
            if (getBoard().positionExists(p) && isThereOpponentPiece(p)){
                mat[p.getRow()][p.getColumn()] = true;
            }

            //Movimento especial en passant
            if (position.getRow()== 4){
                Position esquerda = new Position(position.getRow(), position.getColumn() - 1);
                if (getBoard().positionExists(esquerda) && isThereOpponentPiece(esquerda) && getBoard().piece(esquerda) == chessMatch.getEnPassantVulnerable()){
                    mat[esquerda.getRow() + 1][esquerda.getColumn()] = true;
                }
                Position direita = new Position(position.getRow(), position.getColumn() + 1);
                if (getBoard().positionExists(direita) && isThereOpponentPiece(direita) && getBoard().piece(direita) == chessMatch.getEnPassantVulnerable()){
                    mat[direita.getRow() + 1][direita.getColumn()] = true;
                }
            }
        }

        return mat;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connect4round2;

/**
 *
 * @author Kristen
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


enum Piece
{
    Red(Color.RED),
    Yellow(Color.YELLOW),
    None(Color.WHITE);

    public final Color pieceColor;
    private Piece(Color theColor)
    {
        this.pieceColor = theColor;
    }
}
final class Board extends JButton
{
    public int i, j;
    public Piece piece = Piece.None;

    public Board(int i, int j)
    {
        this.i = i;
        this.j = j;
        setOpaque(true);
        updateColor();
    }
    public void setPiece(Piece piece)
    {
        this.piece = piece;
        updateColor();
    }
    public void updateColor()
    {
        setBackground(piece.pieceColor);
    }
}

final class MiniMax
{
    Random rand = new Random();
    public int moveScore;
    Board[][] board;
    private ArrayList<Integer> bestMoves;
    Board prev = null;
    int depth;
    int maxDepth = 4;

    public MiniMax(Board[][] board, int depth)
    {
        this.board = board;
        this.bestMoves = new ArrayList<>();
        this.depth = depth;
        this.moveScore = score();

        if(depth < maxDepth && this.moveScore < 100 && this.moveScore > -100 )
        {
            ArrayList<Integer> possibilities = new ArrayList<>();
            for(int i = 0; i < 7; i++)
            {
                if(board[i][0].piece == Piece.None)
                {
                    possibilities.add(i);
                }
            }

            for(int i = 0; i < possibilities.size(); i++)
            {
                play(board[possibilities.get(i)][0]);
                MiniMax child = new MiniMax(board, depth+1);
                prev.setPiece(Piece.None);

                if(i == 0)
                {
                    bestMoves.add(possibilities.get(i));
                    moveScore = child.moveScore;
                }
                else if(depth % 2 == 0)
                {
                    if(moveScore < child.moveScore)
                    {
                        bestMoves.clear();
                        bestMoves.add(possibilities.get(i));
                        this.moveScore = child.moveScore;
                    }
                    else if(moveScore == child.moveScore)
                        bestMoves.add(possibilities.get(i));
                }
                else if(depth % 2 == 1)
                {
                    if(moveScore > child.moveScore)
                    {
                        bestMoves.clear();
                        bestMoves.add(possibilities.get(i));
                        this.moveScore = child.moveScore;
                    }
                    else if(moveScore == child.moveScore)
                        bestMoves.add(possibilities.get(i));
                }
            }
        }
        else
        {
            this.moveScore = score();
        }
    }

    void play(Board Board)
    {
        if(Board.piece != Piece.None)
            return;

        int i = Board.i;
        int j = Board.j;

        while(j < board[0].length-1 && board[i][j+1].piece == Piece.None)
            j++;

        if(depth % 2 == 0)
            board[i][j].setPiece(Piece.Red);
        else
            board[i][j].setPiece(Piece.Yellow);
        prev = board[i][j];
    }

    public int playerMove()
    {
        int random = (int)(Math.random() * bestMoves.size());
        return bestMoves.get(random);
    }

    public int score() // get the moveScore of each move
    {
        int bestScore = 0;
        for(int j = 0; j < 6; j++)
        {
            for(int i = 0; i < 7; i++)
            {
                if(board[i][j].piece != Piece.None)
                {
                    if(board[i][j].piece == Piece.Red)
                    {
                        bestScore += possibleWins(i, j) * (maxDepth - this.depth);
                    }
                    else
                    {
                        bestScore -= possibleWins(i, j) * (maxDepth - this.depth);
                    }
                }
            }
        }
        return bestScore;
    }

    public int possibleWins(int i, int j)
    {
        int value = 0;
        value += lineOfFour(i, j, -1, -1);
        value += lineOfFour(i, j, -1, 0);
        value += lineOfFour(i, j, -1, 1);
        value += lineOfFour(i, j, 0, -1);
        value += lineOfFour(i, j, 0, 1);
        value += lineOfFour(i, j, 1, -1);
        value += lineOfFour(i, j, 1, 0);
        value += lineOfFour(i, j, 1, 1);

        return value;
    }

    public int lineOfFour(int x, int y, int i, int j)
    {
        int value = 1;
        Piece color = board[x][y].piece;

        for(int k = 1; k < 4; k++)
        {
            if(x+i*k < 0 || y+j*k < 0 || x+i*k >= board.length || y+j*k >= board[0].length)
                return 0;
            if(board[x+i*k][y+j*k].piece == color)
                value++;
            else if (board[x+i*k][y+j*k].piece != Piece.None)
                return 0;
            else
            {
                for(int l = y+j*k; l >= 0; l--)
                    if(board[x+i*k][l].piece == Piece.None)
                        value--;
            }
        }

        if(value == 4) return 100;
        if(value < 0) return 0;
        return value;
    }

    int randomMove(){
        return bestMoves.get(rand.nextInt(bestMoves.size()));
    }
}

public final class Connect4Round2 extends JFrame implements ActionListener
{
    JLabel lblPlayer = new JLabel("Player: ");
    JLabel lblCurrentPlayer = new JLabel("Yellow");
    JPanel pnlMenu = new JPanel();
    JPanel pnlBoards = new JPanel();
    JButton btnNewGame = new JButton("New Game");

    Board[][] board = new Board[7][6];
    
    boolean winnerExists = false;
    boolean tieExists = false;
    int currentPlayer = 0;
    boolean AI = true;

    public Connect4Round2(boolean AI)
    {
        super("Four In A Row");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        currentPlayer = (int)((Math.random()*2) + 1)% 2;

        btnNewGame.addActionListener(this);
        switch(currentPlayer)
        {
            case 1:
                lblCurrentPlayer.setForeground(Color.YELLOW);
                lblCurrentPlayer.setText("Yellow");
                break;
            case 2:
                lblCurrentPlayer.setForeground(Color.RED);
                lblCurrentPlayer.setText("Red");
                break;
        }
        pnlMenu.add(btnNewGame);
        pnlMenu.add(lblPlayer);
        pnlMenu.add(lblCurrentPlayer);

        pnlBoards.setLayout(new GridLayout(6, 7));

        for(int j = 0; j < 6; j++)
            for(int i = 0; i < 7; i++)
            {
                board[i][j] = new Board(i, j);
                board[i][j].addActionListener(this);
                pnlBoards.add(board[i][j]);
            }

        add(pnlMenu, BorderLayout.NORTH);
        add(pnlBoards, BorderLayout.CENTER);    
        setSize(500, 500);
        setVisible(true);

        if(currentPlayer == 2 && AI) insertTo(minimax());
    }

    @Override
    public void actionPerformed(ActionEvent ae)
    {

        if(ae.getSource() == btnNewGame)
        {
            if(JOptionPane.showConfirmDialog(this, "Ready for a new game?", "Confirmation", JOptionPane.YES_NO_OPTION) == 0)
            {
                dispose();
                Connect4Round2 connect4Round2 = new Connect4Round2(true);
            }
        }
        else if(!winnerExists)
        {
            Board Board = (Board)ae.getSource();
            insertTo(Board);
        }
    }

    void insertTo(Board Board)
    {
        if(Board.piece != Piece.None)
            return;

        int i = Board.i;
        int j = Board.j;

        while(j < board[0].length-1 && board[i][j+1].piece == Piece.None)
            j++;

        switch(currentPlayer)
        {
            case 1:
                board[i][j].setPiece(Piece.Yellow);
                break;
            case 2:
                board[i][j].setPiece(Piece.Red);
                break;
        }

        currentPlayer = (currentPlayer % 2) + 1;

        if(winner())
        {
            lblPlayer.setText("Winner: ");
            winnerExists = true;
        }
        else
        {
            switch(currentPlayer)
            {
                case 1:
                    lblCurrentPlayer.setForeground(Color.YELLOW);
                    lblCurrentPlayer.setText("Yellow");
                    break;
                case 2:
                    lblCurrentPlayer.setForeground(Color.RED);
                    lblCurrentPlayer.setText("Red");
                    break;
            }

            if(currentPlayer == 2 && AI)
            {
                insertTo(minimax());
            }
        }
    }

    public boolean winner()
    {
        for(int j = 0; j < 6; j++)
        {
            for(int i = 0; i < 7; i++)
            {
                if(board[i][j].piece != Piece.None && connectsToFour(i, j))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean connectsToFour(int i, int j)
    {
        if(lineOfFour(i, j, -1, -1))
            return true;
        if(lineOfFour(i, j, -1, 0))
            return true;
        if(lineOfFour(i, j, -1, 1))
            return true;
        if(lineOfFour(i, j, 0, -1))
            return true;
        if(lineOfFour(i, j, 0, 1))
            return true;
        if(lineOfFour(i, j, 1, -1))
            return true;
        if(lineOfFour(i, j, 1, 0))
            return true;
        return lineOfFour(i, j, 1, 1);
    }

    public boolean lineOfFour(int x, int y, int i, int j)
    {
        Piece color = board[x][y].piece;

        for(int k = 1; k < 4; k++)
        {
            if(x+i*k < 0 || y+j*k < 0 || x+i*k >= board.length || y+j*k >= board[0].length)
                return false;
            if(board[x+i*k][y+j*k].piece != color)
                return false;
        }
        return true;
    }

    public Board minimax()
    {
        MiniMax minimax = new MiniMax(board, 0);
        return board[minimax.playerMove()][0];
    }

    public static void main(String[] args)
    {
        new Connect4Round2(false);
    }
}   


package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


/**
 * {@link Grid} instances represent the grid in <i>The Game of Life</i>.
 */
public class Grid implements Iterable<Cell> {

    private final int numberOfRows;
    private final int numberOfColumns;
    private final Cell[][] cells;

    /**
     * Creates a new {@code Grid} instance given the number of rows and columns.
     *
     * @param numberOfRows    the number of rows
     * @param numberOfColumns the number of columns
     * @throws IllegalArgumentException if {@code numberOfRows} or {@code numberOfColumns} are
     *                                  less than or equal to 0
     */
    public Grid(int numberOfRows, int numberOfColumns) {
        this.numberOfRows = numberOfRows;
        this.numberOfColumns = numberOfColumns;
        this.cells = createCells();
    }

    /**
     * Returns an iterator over the cells in this {@code Grid}.
     *
     * @return an iterator over the cells in this {@code Grid}
     */

    @Override
    public Iterator<Cell> iterator() {
        return new GridIterator(this);
    }

    private Cell[][] createCells() {
        Cell[][] cells = new Cell[getNumberOfRows()][getNumberOfColumns()];
        for (int rowIndex = 0; rowIndex < getNumberOfRows(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < getNumberOfColumns(); columnIndex++) {
                cells[rowIndex][columnIndex] = new Cell();
            }
        }
        return cells;
    }

    /**
     * Returns the {@link Cell} at the given index.
     *
     * <p>Note that the index is wrapped around so that a {@link Cell} is always returned.
     *
     * @param rowIndex    the row index of the {@link Cell}
     * @param columnIndex the column index of the {@link Cell}
     * @return the {@link Cell} at the given row and column index
     */
    public Cell getCell(int rowIndex, int columnIndex) {
        return cells[getWrappedRowIndex(rowIndex)][getWrappedColumnIndex(columnIndex)];
    }

    private int getWrappedRowIndex(int rowIndex) {
        return (rowIndex + getNumberOfRows()) % getNumberOfRows();
    }

    private int getWrappedColumnIndex(int columnIndex) {
        return (columnIndex + getNumberOfColumns()) % getNumberOfColumns();
    }

    /**
     * Returns the number of rows in this {@code Grid}.
     *
     * @return the number of rows in this {@code Grid}
     */
    public int getNumberOfRows() {
        return numberOfRows;
    }

    /**
     * Returns the number of columns in this {@code Grid}.
     *
     * @return the number of columns in this {@code Grid}
     */
    public int getNumberOfColumns() {
        return numberOfColumns;
    }


    private List<Cell> getNeighbours(int rowIndex, int columnIndex) {
        List<Cell> cells = new ArrayList<Cell>(8);
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++)
                if (i != 0 || j != 0)
                    cells.add(this.getCell((rowIndex + i) % this.getNumberOfRows(), (columnIndex + j) % this.getNumberOfColumns()));
        }
        return cells;
    }

    private int countAliveNeighbours(int rowIndex, int columnIndex) {
        int count = 0;
        for (Cell cell: getNeighbours(rowIndex, columnIndex)) {
            if (cell.isAlive())
                count++;
        }
        return count;
    }

    private CellState calculateNextState(int rowIndex, int columnIndex) {
        int count = countAliveNeighbours(rowIndex, columnIndex);
        CellState state = this.getCell(rowIndex, columnIndex).getState();
        if ((state.isAlive && (count == 2 || count == 3)) || (!state.isAlive && count == 3)) {
            if (state == CellState.DEAD)
                state = CellState.ALIVE;
            else
                state = CellState.DEAD;
        }
        return state;
    }


    private CellState[][] calculateNextStates() {
        CellState[][] nextCellState = new CellState[getNumberOfRows()][getNumberOfColumns()];
        for (int i = 0; i < getNumberOfRows(); i++) {
            for (int j = 0; j < getNumberOfColumns(); j++) {
                nextCellState[i][j] = calculateNextState(i, j);
            }
        }
        return nextCellState;
    }


    private void updateStates(CellState[][] nextState) {
        for (int i = 0; i < getNumberOfRows(); i++) {
            for (int j = 0; j < getNumberOfColumns(); j++) {
                cells[i][j].setState(nextState[i][j]);
            }
        }
    }

    /**
     * Transitions all {@link Cell}s in this {@code Grid} to the next generation.
     *
     * <p>The following rules are applied:
     * <ul>
     * <li>Any live {@link Cell} with fewer than two live neighbours dies, i.e. underpopulation.</li>
     * <li>Any live {@link Cell} with two or three live neighbours lives on to the next
     * generation.</li>
     * <li>Any live {@link Cell} with more than three live neighbours dies, i.e. overpopulation.</li>
     * <li>Any dead {@link Cell} with exactly three live neighbours becomes a live cell, i.e.
     * reproduction.</li>
     * </ul>
     */

    void updateToNextGeneration() {
        updateStates(calculateNextStates());
    }

    /**
     * Sets all {@link Cell}s in this {@code Grid} as dead.
     */

    void clear() {
        for (int i = 0; i < getNumberOfRows(); i++)
            for (int j = 0; j < getNumberOfColumns(); j++)
                cells[i][j].setState(CellState.DEAD);
    }

    /**
     * Goes through each {@link Cell} in this {@code Grid} and randomly sets its state as ALIVE or DEAD.
     *
     * @param random {@link Random} instance used to decide if each {@link Cell} is ALIVE or DEAD.
     * @throws NullPointerException if {@code random} is {@code null}.
     */

    void randomGeneration(Random random) {
        for (int i = 0; i < getNumberOfRows(); i++) {
            for (int j = 0; j < getNumberOfColumns(); j++) {
                if (random.nextBoolean())
                    cells[i][j].setState(CellState.ALIVE);
                else
                    cells[i][j].setState(CellState.DEAD);
            }
        }
    }
}

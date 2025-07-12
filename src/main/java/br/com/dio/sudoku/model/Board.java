package br.com.dio.sudoku.model;

import java.util.Collection;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Board {

    private final List<List<Space>> spaces;


    public Board(List<List<Space>> spaces) {
        this.spaces = spaces;
    }

    public List<List<Space>> getSpaces() {
        return spaces;
    }

    public GameStatus getGameStatus() {
        if (spaces.stream().flatMap(List::stream).noneMatch(b -> !b.isFixed() && nonNull(b.getActual()))) {
            return GameStatus.NON_INITIALIZED;
        }

        return spaces.stream().flatMap(Collection::stream).anyMatch(s -> isNull(s.getActual())) ? GameStatus.INCOMPLETE : GameStatus.COMPLETED;
    }

    public boolean hasError() {
        if (getGameStatus() == GameStatus.NON_INITIALIZED) return false;
        return spaces.stream().flatMap(Collection::stream).anyMatch(s -> nonNull(s.getActual()) && !s.getActual().equals(s.getExpected()));
    }

    public boolean changeValue(final int col, final int row, final int value) {
        Space space = spaces.get(col).get(row);
        if (space.isFixed()) return false;
        space.setActual(value);
        return true;
    }

    public boolean clearValue(final int col, final int row) {
        final Space space = spaces.get(col).get(row);
        if (space.isFixed()) return false;
        space.clearSpace();
        return true;
    }

    public void reset() {
        spaces.forEach(c -> c.forEach(Space::clearSpace));
    }

    public boolean gameIsFinished() {
        return !hasError() && getGameStatus() == GameStatus.COMPLETED;
    }
}

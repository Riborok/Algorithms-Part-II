package seam;

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private static class EnergyNode implements Comparable<EnergyNode>{
        private final double energy;
        private final int prev;
        private EnergyNode(double energy, int prev) {
            this.energy = energy;
            this.prev = prev;
        }
        @Override
        public int compareTo(EnergyNode o) { return Double.compare(this.energy, o.energy); }
    }

    private static final double BORDER_PIXEL_ENERGY = 1000;

    private Picture picture;

    private boolean isBorder(int row, int col) { return col == 0 || col == width() - 1 || row == 0 || row == height() - 1; }

    private void checkSeam(int[] seam, int length, int max) {
        if (seam == null || seam.length != length)
            throw new IllegalArgumentException();

        for (int i = 0; i < length; i++)
            if (seam[i] < 0 || seam[i] >= max || (i > 0 && Math.abs(seam[i] - seam[i - 1]) > 1))
                throw new IllegalArgumentException();
    }

    private boolean isValidPixel(int row, int col) { return col > -1 && col < width() && row > -1 && row < height(); }

    private void checkCoords(int row, int col){
        if (!isValidPixel(row, col))
            throw new IllegalArgumentException();
    }

    private double getEnergyFromPixel(EnergyNode[][] energies, int row, int col) {
        return isValidPixel(row, col) ? energies[row][col].energy : Double.MAX_VALUE;
    }

    private void updateEnergyNode(EnergyNode[][] energies, int row, int col, double newEnergy, int prevVert) {
        energies[row][col] = new EnergyNode(newEnergy, prevVert);
    }

    private void updateEnergyNode(EnergyNode[][] energies, int row, int col, double energy, double option1, int prevVert1, double option2, int prevVert2, double option3, int prevVert3) {
        double minValue = Math.min(option1, Math.min(option2, option3));

        if (minValue == option1)
            updateEnergyNode(energies, row, col, option1 + energy, prevVert1);
        else if (minValue == option2)
            updateEnergyNode(energies, row, col, option2 + energy, prevVert2);
        else
            updateEnergyNode(energies, row, col, option3 + energy, prevVert3);
    }

    private void relaxVertical(EnergyNode[][] energies, int row, int col) {
        double energy = energy(col, row);

        double leftEnergy = getEnergyFromPixel(energies, row - 1, col - 1);
        double topEnergy = getEnergyFromPixel(energies, row - 1, col);
        double rightEnergy = getEnergyFromPixel(energies, row - 1, col + 1);

        updateEnergyNode(energies, row, col, energy, leftEnergy, col - 1, topEnergy, col, rightEnergy, col + 1);
    }

    private void relaxHorizontal(EnergyNode[][] energies, int row, int col) {
        double energy = energy(col, row);

        double topEnergy = getEnergyFromPixel(energies, row - 1, col - 1);
        double middleEnergy = getEnergyFromPixel(energies, row, col - 1);
        double bottomEnergy = getEnergyFromPixel(energies, row + 1, col - 1);

        updateEnergyNode(energies, row, col, energy, topEnergy, row - 1, middleEnergy, row, bottomEnergy, row + 1);
    }

    private int findLowestEnergyIndexVertical(EnergyNode[][] energies, int row) {
        double lowestEnergy = Double.MAX_VALUE;
        int index = -1;

        for (int col = 0; col < width(); col++) {
            if (energies[row][col].energy < lowestEnergy) {
                lowestEnergy = energies[row][col].energy;
                index = col;
            }
        }

        return index;
    }

    private int[] backtrackSeamVertical(EnergyNode[][] energies, int startCol) {
        int[] seam = new int[height()];
        int index = startCol;
        int row = height() - 1;

        while (row > -1) {
            seam[row] = index;
            index = energies[row][index].prev;
            row--;
        }

        return seam;
    }

    private int[] createVerticalSeam(EnergyNode[][] energies){
        int startCol = findLowestEnergyIndexVertical(energies, height() - 1);
        return backtrackSeamVertical(energies, startCol);
    }

    private int findLowestEnergyIndexHorizontal(EnergyNode[][] energies, int col) {
        double lowestEnergy = Double.MAX_VALUE;
        int index = -1;

        for (int row = 0; row < height(); row++) {
            if (energies[row][col].energy < lowestEnergy) {
                lowestEnergy = energies[row][col].energy;
                index = row;
            }
        }

        return index;
    }

    private int[] backtrackSeamHorizontal(EnergyNode[][] energies, int startRow) {
        int[] seam = new int[width()];
        int index = startRow;
        int col = width() - 1;

        while (col > -1) {
            seam[col] = index;
            index = energies[index][col].prev;
            col--;
        }

        return seam;
    }

    private int[] createHorizontalSeam(EnergyNode[][] energies) {
        int startRow = findLowestEnergyIndexHorizontal(energies, width() - 1);
        return backtrackSeamHorizontal(energies, startRow);
    }

    private EnergyNode[][] initializeVerticalEnergies() {
        EnergyNode[][] energies = new EnergyNode[height()][width()];
        for (int i = 0; i < width(); i++)
            energies[0][i] = new EnergyNode(BORDER_PIXEL_ENERGY, -1);
        return energies;
    }

    private void fillVerticalEnergies(EnergyNode[][] energies) {
        for (int row = 1; row < height(); row++){
            energies[row][0] = new EnergyNode(BORDER_PIXEL_ENERGY, -1);
            for (int col = 0; col < width(); col++)
                relaxVertical(energies, row, col);
        }
    }

    private EnergyNode[][] initializeHorizontalEnergies() {
        EnergyNode[][] energies = new EnergyNode[height()][width()];
        for (int i = 0; i < height(); i++)
            energies[i][0] = new EnergyNode(BORDER_PIXEL_ENERGY, -1);
        return energies;
    }

    private void fillHorizontalEnergies(EnergyNode[][] energies) {
        for (int col = 1; col < width(); col++) {
            energies[0][col] = new EnergyNode(BORDER_PIXEL_ENERGY, -1);
            for (int row = 0; row < height(); row++)
                relaxHorizontal(energies, row, col);
        }
    }

    public SeamCarver(Picture picture){
        if (picture == null)
            throw new IllegalArgumentException();

        this.picture = new Picture(picture);
    }

    public Picture picture(){ return new Picture(this.picture); }

    public int width(){ return picture.width(); }

    public int height(){ return picture.height(); }

    public double energy(int x, int y){
        checkCoords(y, x);

        if (isBorder(y, x))
            return BORDER_PIXEL_ENERGY;

        var l = picture.get(x - 1, y);
        var r = picture.get(x + 1, y);
        var u = picture.get(x, y - 1);
        var b = picture.get(x, y + 1);

        double rx = r.getRed() - l.getRed();
        double gx = r.getGreen() - l.getGreen();
        double bx = r.getBlue() - l.getBlue();
        double ry = b.getRed() - u.getRed();
        double gy = b.getGreen() - u.getGreen();
        double by = b.getBlue() - u.getBlue();

        return Math.sqrt(rx * rx + gx * gx + bx * bx + ry * ry + gy * gy + by * by);
    }

    public int[] findHorizontalSeam(){
        EnergyNode[][] energies = initializeHorizontalEnergies();
        fillHorizontalEnergies(energies);
        return createHorizontalSeam(energies);
    }

    public int[] findVerticalSeam(){
        EnergyNode[][] energies = initializeVerticalEnergies();
        fillVerticalEnergies(energies);
        return createVerticalSeam(energies);
    }

    public void removeHorizontalSeam(int[] seam) {
        checkSeam(seam, width(), height());

        Picture newPicture = new Picture(width(), height() - 1);
        for (int col = 0; col < width(); col++) {
            int newCol = 0;
            for (int row = 0; row < height(); row++) {
                if (row != seam[col]) {
                    newPicture.set(col, newCol, picture.get(col, row));
                    newCol++;
                }
            }
        }
        picture = newPicture;
    }

    public void removeVerticalSeam(int[] seam) {
        checkSeam(seam, height(), width());

        Picture newPicture = new Picture(width() - 1, height());
        for (int row = 0; row < height(); row++) {
            int newRow = 0;
            for (int col = 0; col < width(); col++) {
                if (col != seam[row]) {
                    newPicture.set(newRow, row, picture.get(col, row));
                    newRow++;
                }
            }
        }
        picture = newPicture;
    }
}
package baseball;

import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseballElimination {
    private final Map<String, Integer> teamIndices = new HashMap<>();
    private int numOfTeams;
    private String[] teams;
    private int[] wins;
    private int[] losses;
    private int[] remaining;
    private int[][] against;

    public BaseballElimination(String filename) {
        In in = new In(filename);
        readNumberOfTeamsFromFile(in);
        createTeamStats();
        readStatsFromFile(in);
    }

    private void readNumberOfTeamsFromFile(In in) {
        numOfTeams = in.readInt();
    }

    private void createTeamStats() {
        teams = new String[numOfTeams];
        wins = new int[numOfTeams];
        losses = new int[numOfTeams];
        remaining = new int[numOfTeams];
        against = new int[numOfTeams][numOfTeams];
    }

    private void readStatsFromFile(In in) {
        for (int i = 0; i < numOfTeams; i++)
            readTeamStatFromFile(in, i);
    }

    private void readTeamStatFromFile(In in, int teamIndex) {
        String team = in.readString();
        teamIndices.put(team, teamIndex);
        teams[teamIndex] = team;
        wins[teamIndex] = in.readInt();
        losses[teamIndex] = in.readInt();
        remaining[teamIndex] = in.readInt();
        readAgainstFromFile(in, teamIndex);
    }

    private void readAgainstFromFile(In in, int teamIndex) {
        for (int i = 0; i < numOfTeams; i++)
            against[teamIndex][i] = in.readInt();
    }

    public int numberOfTeams() {
        return numOfTeams;
    }

    public Iterable<String> teams() {
        return List.of(teams);
    }

    public int wins(String team) {
        validateTeam(team);
        return wins[teamIndices.get(team)];
    }

    public int losses(String team) {
        validateTeam(team);
        return losses[teamIndices.get(team)];
    }

    public int remaining(String team) {
        validateTeam(team);
        return remaining[teamIndices.get(team)];
    }

    public int against(String team1, String team2) {
        validateTeam(team1);
        validateTeam(team2);
        return against[teamIndices.get(team1)][teamIndices.get(team2)];
    }

    public boolean isEliminated(String team) {
        validateTeam(team);
        var certificate = findEliminationCertificate(teamIndices.get(team));
        return !certificate.isEmpty();
    }

    public Iterable<String> certificateOfElimination(String team) {
        validateTeam(team);
        var certificate = findEliminationCertificate(teamIndices.get(team));
        return certificate.isEmpty() ? null : certificate;
    }

    private void validateTeam(String team) {
        if (!hasTeam(team))
            throw new IllegalArgumentException();
    }

    private boolean hasTeam(String team) {
        return teamIndices.containsKey(team);
    }

    private List<String> findEliminationCertificate(int teamIndex) {
        List<String> certificate = new ArrayList<>();
        if (isTeamEliminatedWithoutRemainingGamesPlayedBetweenOthers(teamIndex, certificate))
            return certificate;

        var flowNetwork = new FlowNetwork(calcNumOfVertices());

        addGameVertices(flowNetwork, teamIndex);
        addTeamVertices(flowNetwork, teamIndex);

        findEliminationCertificate(flowNetwork, teamIndex, certificate);
        return certificate;
    }

    private boolean isTeamEliminatedWithoutRemainingGamesPlayedBetweenOthers(int teamIndex, List<String> certificate) {
        for (int i = 0; i < numOfTeams; i++)
            if (!isAchievableTeamWithoutRemainingGamesPlayedBetweenOthers(teamIndex, i))
                certificate.add(teams[i]);
        return !certificate.isEmpty();
    }

    private boolean isAchievableTeamWithoutRemainingGamesPlayedBetweenOthers(int teamIndex, int targetTeam) {
        return wins[teamIndex] + remaining[teamIndex] >= wins[targetTeam];
    }

    private void addGameVertices(FlowNetwork flowNetwork, int teamIndex) {
        int numOfGameVertices = calcNumOfGameVertices();
        int sourceVertex = getSourceVertex();
        int gameVertex = sourceVertex + 1;

        for (int i = 0; i < numOfTeams; i++) {
            if (isNotCheckableTeam(teamIndex, i)) {
                for (int j = i + 1; j < numOfTeams; j++) {
                    if (isNotCheckableTeam(teamIndex, j)) {
                        int teamVertex1 = calcTeamVertex(numOfGameVertices, i);
                        int teamVertex2 = calcTeamVertex(numOfGameVertices, j);
                        addGameEdges(flowNetwork, sourceVertex, gameVertex, teamVertex1, teamVertex2, against[i][j]);
                        gameVertex++;
                    }
                }
            }
        }
    }

    private void addGameEdges(FlowNetwork flowNetwork, int sourceVertex, int gameVertex,
                              int teamVertex1, int teamVertex2, int againstValue) {
        flowNetwork.addEdge(new FlowEdge(sourceVertex, gameVertex, againstValue));
        flowNetwork.addEdge(new FlowEdge(gameVertex, teamVertex1, Double.POSITIVE_INFINITY));
        flowNetwork.addEdge(new FlowEdge(gameVertex, teamVertex2, Double.POSITIVE_INFINITY));
    }

    private void addTeamVertices(FlowNetwork flowNetwork, int teamIndex) {
        int numOfGameVertices = calcNumOfGameVertices();
        int targetVertex = calcTargetVertex();

        for (int i = 0; i < numOfTeams; i++)
            if (isNotCheckableTeam(teamIndex, i)) {
                int teamVertex = calcTeamVertex(numOfGameVertices, i);
                int capacity = calcCapacity(teamIndex, i);
                addTeamEdge(flowNetwork, teamVertex, targetVertex, capacity);
            }
    }

    private int calcCapacity(int teamIndex, int otherTeamIndex) {
        return wins[teamIndex] + remaining[teamIndex] - wins[otherTeamIndex];
    }

    private void addTeamEdge(FlowNetwork flowNetwork, int teamVertex, int targetVertex, int capacity) {
        flowNetwork.addEdge(new FlowEdge(teamVertex, targetVertex, capacity));
    }

    private void findEliminationCertificate(FlowNetwork flowNetwork, int teamIndex, List<String> certificate) {
        int numOfGameVertices = calcNumOfGameVertices();
        FordFulkerson fordFulkerson = new FordFulkerson(flowNetwork, getSourceVertex(), calcTargetVertex());

        for (int i = 0; i < numOfTeams; i++)
            if (isEliminatedTeamInCut(teamIndex, i, fordFulkerson, numOfGameVertices))
                certificate.add(teams[i]);
    }

    private boolean isEliminatedTeamInCut(int teamIndex, int checkedTeamIndex,
                                          FordFulkerson fordFulkerson, int numOfGameVertices) {
        return isNotCheckableTeam(teamIndex, checkedTeamIndex) &&
                fordFulkerson.inCut(calcTeamVertex(numOfGameVertices, checkedTeamIndex));
    }

    private int calcTeamVertex(int numOfGameVertices, int teamIndex) {
        return numOfGameVertices + teamIndex;
    }

    private boolean isNotCheckableTeam(int teamIndex, int otherTeamIndex) {
        return teamIndex != otherTeamIndex;
    }

    private int getSourceVertex() {
        return 0;
    }

    private int calcTargetVertex() {
        return calcNumOfVertices() - 1;
    }

    private int calcNumOfVertices() {
        return 2 + calcNumOfGameVertices() + calcNumOfTeamVertices();
    }

    private int calcNumOfTeamVertices() {
        return numOfTeams - 1;
    }

    private int calcNumOfGameVertices() {
        return numOfTeams * (numOfTeams - 1) / 2;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
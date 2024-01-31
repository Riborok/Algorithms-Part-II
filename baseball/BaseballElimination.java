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
    private boolean hasTeam(String team) {
        return teamIndices.containsKey(team);
    }
    private void validateTeam(String team) {
        if (!hasTeam(team))
            throw new IllegalArgumentException();
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
    private void readAgainstFromFile(In in, int teamIndex) {
        for (int i = 0; i < numOfTeams; i++)
            against[teamIndex][i] = in.readInt();
    }
    private void readTeamStatsFromFile(In in, int teamIndex) {
        String team = in.readString();
        teamIndices.put(team, teamIndex);
        teams[teamIndex] = team;
        wins[teamIndex] = in.readInt();
        losses[teamIndex] = in.readInt();
        remaining[teamIndex] = in.readInt();
        readAgainstFromFile(in, teamIndex);
    }
    private void readTeamStatsFromFile(In in) {
        for (int i = 0; i < numOfTeams; i++)
            readTeamStatsFromFile(in, i);
    }
    private boolean isAchievableTeamWithoutRemainingGamesPlayedBetweenOthers(int teamIndex, int targetTeam) {
        return wins[teamIndex] + remaining[teamIndex] >= wins[targetTeam];
    }
    private boolean isTeamEliminatedWithoutRemainingGamesPlayedBetweenOthers(int teamIndex, List<String> certificate) {
        for (int i = 0; i < numOfTeams; i++)
            if (!isAchievableTeamWithoutRemainingGamesPlayedBetweenOthers(teamIndex, i))
                certificate.add(teams[i]);
        return !certificate.isEmpty();
    }
    private boolean isNotCheckableTeam(int teamIndex, int otherTeamIndex) {
        return teamIndex != otherTeamIndex;
    }
    private int calcNumOfTeamVertices() {
        return numOfTeams - 1;
    }
    private int calcNumOfGameVertices() {
        return numOfTeams * (numOfTeams - 1) / 2;
    }
    private int calcNumOfVertices() {
        return 2 + calcNumOfGameVertices() + calcNumOfTeamVertices();
    }
    private int getSourceVertex() {
        return 0;
    }
    private int calcTargetVertex() {
        return calcNumOfVertices() - 1;
    }
    private int calcTeamVertex(int numOfGameVertices, int teamIndex) {
        return numOfGameVertices + teamIndex;
    }
    private void addGameVerticesToFlowNetwork(FlowNetwork flowNetwork, int teamIndex) {
        int numOfGameVertices = calcNumOfGameVertices();
        int sourceVertex = getSourceVertex();
        int gameVertex = sourceVertex + 1;

        for (int i = 0; i < numOfTeams; i++) {
            if (isNotCheckableTeam(teamIndex, i)) {
                for (int j = i + 1; j < numOfTeams; j++) {
                    if (isNotCheckableTeam(teamIndex, j)) {
                        flowNetwork.addEdge(new FlowEdge(sourceVertex, gameVertex, against[i][j]));
                        flowNetwork.addEdge(new FlowEdge(gameVertex, calcTeamVertex(numOfGameVertices, i), Double.POSITIVE_INFINITY));
                        flowNetwork.addEdge(new FlowEdge(gameVertex, calcTeamVertex(numOfGameVertices, j), Double.POSITIVE_INFINITY));
                        gameVertex++;
                    }
                }
            }
        }
    }
    private void addTeamVerticesToFlowNetwork(FlowNetwork flowNetwork, int teamIndex) {
        int numOfGameVertices = calcNumOfGameVertices();
        int targetVertex = calcTargetVertex();

        for (int i = 0; i < numOfTeams; i++) {
            if (isNotCheckableTeam(teamIndex, i)) {
                int capacity = wins[teamIndex] + remaining[teamIndex] - wins[i];
                flowNetwork.addEdge(new FlowEdge(calcTeamVertex(numOfGameVertices, i), targetVertex, capacity));
            }
        }
    }
    private boolean isEliminatedTeamInCut(int teamIndex, int checkedTeamIndex,
                                          FordFulkerson fordFulkerson, int numOfGameVertices) {
        return isNotCheckableTeam(teamIndex, checkedTeamIndex) &&
                fordFulkerson.inCut(calcTeamVertex(numOfGameVertices, checkedTeamIndex));
    }
    private void findEliminationCertificateInFlowNetwork(FlowNetwork flowNetwork, int teamIndex, List<String> certificate) {
        int numOfGameVertices = calcNumOfGameVertices();
        FordFulkerson fordFulkerson = new FordFulkerson(flowNetwork, getSourceVertex(), calcTargetVertex());

        for (int i = 0; i < numOfTeams; i++)
            if (isEliminatedTeamInCut(teamIndex, i, fordFulkerson, numOfGameVertices))
                certificate.add(teams[i]);
    }
    private List<String> findEliminationCertificate(int teamIndex) {
        List<String> certificate = new ArrayList<>();
        if (isTeamEliminatedWithoutRemainingGamesPlayedBetweenOthers(teamIndex, certificate))
            return certificate;

        var flowNetwork = new FlowNetwork(calcNumOfVertices());

        addGameVerticesToFlowNetwork(flowNetwork, teamIndex);
        addTeamVerticesToFlowNetwork(flowNetwork, teamIndex);

        findEliminationCertificateInFlowNetwork(flowNetwork, teamIndex, certificate);
        return certificate;
    }
    public BaseballElimination(String filename) {
        In in = new In(filename);
        readNumberOfTeamsFromFile(in);
        createTeamStats();
        readTeamStatsFromFile(in);
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
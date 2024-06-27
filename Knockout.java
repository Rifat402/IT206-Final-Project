public class Knockout extends SoccerLeague{
    
    public Knockout(int matchLength, String matchDay, int goalArea, String leagueName){
        super(leagueName, matchLength, goalArea, matchDay, 64);
        // maxteams hardcoded at 64 as per specifications
    }

    // variable to track round number / remaining teams, init at 0 to indicate tournament hasnt started
    // losing teams stores teams to be dropped at end of round
    private int round = 128;
    private Team[] allTeams = getTeams();
    private int numPlayed = 0;
    private Team[] losingTeams = new Team[getMaxTeams()];
    private int numLosingTeams = 0;
    private boolean hasStarted = false;

    // get and set for variables
    public int getRound() {return this.round;}
    public void setRound(int round) {this.round = round;}
    public int getNumPlayed(){return this.numPlayed;}
    public void setNumPlayed(int numPlayed){this.numPlayed = numPlayed;}
    
    
    /* addTeam for knockout
    ensures the following before a team can be added: 
        1) team isnt already in the league
        2) the maximum teams has not been reached
        3) the season hasnt already started
        4) the team is not a youth team (4 digit all numeric ID)
     */
    public void addTeam(Team team) {
        if (getRound() < 0){
            throw new IllegalArgumentException("Season has already started, cannot add team");
        }
        if (getNumTeams() >= getMaxTeams())
            throw new IllegalArgumentException("Maximum number of teams per tournament has been reached");
        
        for (int i = 0; i < this.getNumTeams(); i++){
            if (team.getTeamId().equals(teams[i].getTeamId())){
                throw new IllegalArgumentException("Team already exists in tournament");
            }
        }
        if (team.getTeamId().length() != 4){
            throw new IllegalArgumentException("Youth Team cannot join tournament");
        }
        try {
            Integer.parseInt(team.getTeamId());
        } catch (Exception e) {
            throw new IllegalArgumentException("Youth Team cannot join tournament");
        }

        // add team to array and increments numTeams variable
        this.teams[getNumTeams()] = team;
        setNumTeams(getNumTeams() + 1);
        this.allTeams = getTeams();

        // modify round to the nearest power of 2 above the number of teams
        // update ranking for all teams
        setRound((int) Math.pow(2, Math.ceil(Math.log(getNumTeams()) / Math.log(2))));
        for (int i = 0; i < this.allTeams.length; i++){
            if (this.allTeams[i] != null)
                this.allTeams[i].setRanking(getRound());
        }
        
    }


     // removes team from the league
     public void dropTeam(String teamId) {

        for (int i = 0; i < getNumTeams(); i++) {
            if (this.teams[i].getTeamId().equals(teamId)) {
                this.teams[i] = this.teams[getNumTeams() - 1];
                this.teams[getNumTeams() - 1] = null;
                setNumTeams(getNumTeams() - 1);
                return;
            }
        }
        throw new IllegalArgumentException("Team not found");
    }

    // helper method for predictGame() 
    // creates randomized goal number based on team ovr rating 
    // algorithm allows better rated teams to be favored while keeping it mathematically possible for the worse team to win 
    private static int generateScore(double ovr) {

        double baseScore = ovr / 7.5;

        double randomFactor =  Math.random() * 2.0;

        double expectedGoals = baseScore * randomFactor;

        double scaleFactor = 1.2;

        return (int) Math.ceil(expectedGoals * scaleFactor);
    }



    // finds both teams, then uses the helper function to generate goal counts for both teams
    // does not allow draws, adds goal to better side if scores are equal
    public String predictGame(String teamId1, String teamId2) {
        int team1 = -1;
        int team2 = -1;

        for (int i = 0; i < getNumTeams(); i++) {

            if (this.teams[i].getTeamId().equals(teamId1)){
                team1 = i;
            }
            else if (this.teams[i].getTeamId().equals(teamId2)){
                team2 = i;
            }
        }
        
        if (team1 < 0 || team2 < 0){
            throw new IllegalArgumentException("Invalid Team ID entered");
        }

        int score1 = generateScore(this.teams[team1].getOverallRating());
        int score2 = generateScore(this.teams[team2].getOverallRating());

        // prevent draw logic
        while (score1 == score2){
            if (teams[team1].getOverallRating() == teams[team2].getOverallRating()){
                score1 += 1+ (int)(Math.random() * 2);
                score2 += 1+ (int)(Math.random() * 2);
            }
            else{
                score1 = teams[team1].getOverallRating() > teams[team2].getOverallRating() ? score1 + 1 : score1;
                score2 = teams[team1].getOverallRating() < teams[team2].getOverallRating() ? score2 + 1 : score2;
            }
        }

        return String.format("%s %d - %d %s", 
        this.teams[team1].getTeamName(),score1,score2,this.teams[team2].getTeamName());
    }

    // ensure that the number of teams is a power of 2 above 1
    // used in the implementation class once before teams start being knocked out
    public void validateNumTeams(){
        double log = Math.log(this.allTeams.length) / Math.log(2);
        if (log != (int)log || this.allTeams.length < 2)
            throw new IllegalArgumentException("Invalid number of teams, must be a power of 2");
    }

    // performs backend funtionality of the promptAddMatchDay() function in the implementation class
    // ensures that team and tournament data is updated with ranking for teams, and remaining teams. Also ensures that teams hasnt already been added
    // frontend method is responsible for ensuring that all teams in the league are updated to the latest matchday
    // frontend logic ensures the team arguments are legit existing teams
    public boolean addMatchDayl(Team team1, Team team2, int goalsTeam1, int goalsTeam2) {

        // starting the KO tournament
        if (!this.hasStarted){    
            setRound(getNumTeams());
            this.hasStarted = true;
        }
        
        // check if either of the teams are in the next round
        if (team1.getRanking() != getRound() || team2.getRanking() !=  getRound()){
            throw new IllegalArgumentException("Team has already been updated to current match day");
        }
    
        // updates rank, and adds losing team to a losing teams array
        if (goalsTeam1 > goalsTeam2){
            this.losingTeams[numLosingTeams] = team2;
            team1.setRanking(getRound() / 2);
            setNumTeams(getNumTeams() - 1);
        }
        else if (goalsTeam1 < goalsTeam2){
            this.losingTeams[numLosingTeams] = team1;
            team1.setRanking(getRound() / 2);
            setNumTeams(getNumTeams() - 1);
        }
        else
           throw new IllegalArgumentException("Cannot have a draw in a knockout tournament");
        
        // return true if the matchday is complete
        // drops the losing teams from the league and resets numLosingTeams
        if (numLosingTeams == getNumTeams() / 2){
            setRound(getRound() / 2);
            setNumPlayed(0);
            setNumTeams(getNumTeams() / 2);

            for (int i = 0; i < this.losingTeams.length; i++){
                if (this.losingTeams[i] == null)
                    continue;
                else
                    dropTeam(this.losingTeams[i].getTeamId());
            }
            this.numLosingTeams = 0;
            return true;
        }
        setNumPlayed(getNumPlayed() + 2);
        return false;
    }


    // resets ranking for each team and resets round
    public void endSeason() {
        if (getRound() > 1){
            throw new IllegalArgumentException("Tournament not yet complete");
        }
        for (int i = 0; i < getNumTeams(); i++){
            if (allTeams[i] == null)
                continue;  
            allTeams[i].setRanking(0);
        }
        setRound(getMaxTeams());
    }

    // does not require sorting like League
    // the value i represent the round, j finds all teams who have made it to that round and adds them to output string
    public String createTable() {
        String output = "Place\t|\tTeam\n";
        for (int i = 1; i <= allTeams.length; i*=2){
            for (int j = 0; j < allTeams.length; j++){
                if (allTeams[j].getRanking() == i){
                    output += String.format("%d\t|\t%s\n", allTeams[j].getRanking(),allTeams[j].getTeamName());
                }
            }
        }
        return output;
    }
    
    // acts as a getter method, returns the variable that is used internally
    public boolean hasStarted() {
        return this.hasStarted;
    }


    // Unlike League, tournament does not require matchdays to be completed at once
    // Does not allow teams to continue until all teams have achieved the current round 
    public boolean addMatchDay(Team team1, Team team2, int goalsTeam1, int goalsTeam2){
         // starting the KO tournament
         if (!this.hasStarted){    
            setRound(getNumTeams());
            this.hasStarted = true;
        }
        // ensures competing teams are at the same stage
        if (team1.getRanking() != team2.getRanking()){
            throw new IllegalArgumentException("Teams at different stages cannot play against each other");
        }
        // ensures that all teams have reached next stage before allowing teams to continue (only one check needed since rank team 1 == rank team 2)
        if (team1.getRanking() != getRound()){
            throw new IllegalArgumentException("Not all teams have progressed, cannot continue.");
        }

        if (goalsTeam1 > goalsTeam2){
            this.losingTeams[numLosingTeams] = team2;
            team1.setRanking(getRound() / 2);         
        }
        else if (goalsTeam1 < goalsTeam2){
            this.losingTeams[numLosingTeams] = team1;
            team1.setRanking(getRound() / 2);
        }
        else
           throw new IllegalArgumentException("Cannot have a draw in a knockout tournament");
        
        this.numLosingTeams +=1;

       
        if (this.numLosingTeams == Math.round(getNumTeams() / 2)){
           
            for (int i = 0; i < this.numLosingTeams; i++){
                if (this.losingTeams[i] == null)
                    continue;
                else
                    dropTeam(this.losingTeams[i].getTeamId());
            }
            setRound(getRound() / 2);
            setNumPlayed(0);
            setNumTeams(getRound());
            this.numLosingTeams = 0;
            return true;
        }
        setNumPlayed(getNumPlayed() + 2);
        return false;
    }
}
public class League extends SoccerLeague {

    // constructor
    public League(int matchLength, String matchDay, int goalArea, String leagueName) {
        super(leagueName, matchLength, goalArea, matchDay, 20);
        // Hardcoded maximum teams as per specifications
    }


    // match counter variable and matchday tracking variable
    protected int matches = 0;
    protected Team[] played = new Team[getMaxTeams()];
    protected int numPlayed = 0;

   
 

    // getters
    public int getMatches(){return this.matches;}
    public int getNumPlayed(){return this.numPlayed;}

    public Team[] getPlayed() {
        Team[] aPlayed = new Team[getMaxTeams()];

        for (int i = 0; i < played.length; i++){
            aPlayed[i] = played[i];
        }

        return aPlayed;
    }

    // setters 

    public void setMatches(int matches) {this.matches = matches;}
    public void setNumPlayed(int numPlayed){this.numPlayed = numPlayed;}


    // check if tournament has started

    public boolean hasStarted() {
        return getMatches() > 0;
    }
    // adds team to teams array for the league
    /* 
    ensures the following before a team can be added: 
        1) team isnt already in the league
        2) the maximum teams has not been reached
        3) the season hasnt already started
        4) the team is not a youth team (4 digit all numeric ID)
     */
    public void addTeam(Team team) {
        if (matches > 0){
            throw new IllegalArgumentException("Season has already started, cannot add team");
        }
        if (getNumTeams() >= getMaxTeams())
            throw new IllegalArgumentException("Maximum number of teams per league has been reached");
        
        for (int i = 0; i < this.getNumTeams(); i++){
            if (team.getTeamId().equals(teams[i].getTeamId())){
                throw new IllegalArgumentException("Team already exists in league");
            }
        }
        if (team.getTeamId().length() != 4){
            throw new IllegalArgumentException("Team cannot join league");
        }
        try {
            Integer.parseInt(team.getTeamId());
        } catch (Exception e) {
            throw new IllegalArgumentException("Team cannot join league");
        }

        // add team to array and increments numTeams variable
        this.teams[getNumTeams()] = team;
        setNumTeams(getNumTeams() + 1);

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
    protected int generateScore(double ovr) {

        double baseScore = ovr / 7.5;

        double randomFactor =  Math.random() * 2.0;

        double expectedGoals = baseScore * randomFactor;

        double scaleFactor = this.getGoalArea() / 10;

        
        return (int) Math.ceil(expectedGoals * scaleFactor);
    }


    // finds both teams, then uses the helper function to generate goal counts for both teams
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

       
        return String.format("%s %d - %d %s", 
        this.teams[team1].getTeamName(),score1 , 
        score2, this.teams[team2].getTeamName());
    }

    // helper method for addMatchDay()
    // validates whether the number of teams in the league is legit (must be an even number not less than 4)
    // throws error preventing the addMatchDay() method from continuing if invalid
    public void validateNumTeams(){
        if (getNumTeams() % 2 != 0 || getNumTeams() < 4)
            throw new IllegalArgumentException("Invalid number of teams, must be an even number of teams greater than or equal to 4");
    }

    // performs backend funtionality of the promptAddMatchDay() function in the implementation class
    // ensures that team and league data is updated with goals scored, matches, and points for each team. Also ensures that teams hasnt already been added
    // responsible for storing match in both teams
    // frontend method is responsible for ensuring that all teams in the league are updated to the latest matchday
    // frontend logic ensures the team arguments are legit existing teams
    public boolean addMatchDay(Team team1, Team team2, int goalsTeam1, int goalsTeam2) {
        validateNumTeams();
        for (int i = 0; i < getNumPlayed(); i++){
            // check if teams are in the played array
            if (played[i] == null){
                continue;
            }
            else if (played[i].getTeamId().equals(team1.getTeamId()) || played[i].getTeamId().equals(team2.getTeamId())){
                played = new Team[getMaxTeams()];
                setNumPlayed(0);
                reverseMatch();
                throw new IllegalArgumentException("Team has already been updated to current match day");
            }
        }

         // update played and numPlayed
         this.played[getNumPlayed()] = team1;
         this.played[getNumPlayed() + 1] = team2;
         setNumPlayed(getNumPlayed() + 2);
 
        // distributes points based on scoreline (win = 3, draw = 1, loss = 0)
        if (goalsTeam1 > goalsTeam2)
            team1.setPoints(team1.getPoints() + 3);

        else if (goalsTeam1 < goalsTeam2)
            team2.setPoints(team2.getPoints() + 3);

        else{
            team1.setPoints(team1.getPoints() + 1);
            team2.setPoints(team2.getPoints() + 1);
        }
        // updates goal differences of both teams based on match and stores match data
        team1.setGoalDiff(team1.getGoalDiff() + (goalsTeam1 - goalsTeam2));
        team2.setGoalDiff(team2.getGoalDiff() + (goalsTeam2 - goalsTeam1));

        team1.storeMatch(team2.getTeamId(), goalsTeam1, goalsTeam2);
        team2.storeMatch(team1.getTeamId(), goalsTeam1, goalsTeam2);
       
        // reset/clear the played array if the matchday is complete (if all teams have played)
        // return true if the matchday is complete
        if (getNumPlayed() == getNumTeams()){
            played = new Team[getMaxTeams()];
            setNumPlayed(0);
            return true;
        }

        // return false indicating that there are still teams that need to play
        return false;
    }

    // ends season, clears all league and team data
    // sets team goals and points to zero and sets matches to zero
    // removes the bottom 15% of teams from league
    public void endSeason() {
        if (this.matches < (int)(0.25) * getNumTeams()){
            throw new IllegalArgumentException("League has not been played to at least 25% completion, cannot end league.");
        }

        for (int i = 0; i < getNumTeams(); i++){
            teams[i].setPoints(0);
            teams[i].setGoalDiff(0);
        }
        
        Team[] sortedTeams = sortTeams();
        int relagatedTeams = (int)(getNumTeams() -  (getNumTeams() * .15));

        // remove relagated teams
    
        for (int i = relagatedTeams; i < getNumTeams(); i++){
            dropTeam(sortedTeams[i].getTeamId());
        }

    }

    // helper method for addMatchDay() and promptAddMatchDay(), to be used upon error while inserting matchdays
    // reverses all match data modifications caused by erroneous attempt at entering matchday
    public void reverseMatch()
    {
        int extraMatches = -1;
        Team[] teams = getTeams();

        // sets the number to look for to find teams who have played an extra match
        for (int i = 0; i < getNumTeams(); i++)
        {
            extraMatches = extraMatches < teams[i].getNumMatches() ? teams[i].getNumMatches() : extraMatches;
        }

        for (int i = 0; i < extraMatches; i++)
        {
            if (teams[i].getNumMatches() == extraMatches)
            {
                //reverses points earned
                teams[i].setPoints(teams[i].getPoints() - teams[i].getMatchesPlayed()[extraMatches - 1][1]);

                // reverse goal difference
                teams[i].setGoalDiff(teams[i].getGoalDiff() - (teams[i].getMatchesPlayed()[extraMatches - 1][4]));

                // removes match record
                teams[i].setNumMatches(teams[i].getNumMatches() - 1);
                teams[i].removeMatch(teams[i].getNumMatches());
            }

        }

    }

    // displays league data as a table
    public String createTable() {
        Team[] sortedTeams = sortTeams();
        String output = String.format("*** %s ***\nPos\tName\tPoints\tGD\n",this.getLeagueName());
        for (int i = 0; i < sortedTeams.length; i++){
            if (sortedTeams[i] == null){
                continue;
            }
            output += String.format("%d\t%s\t%d\t%d\n", 
            i + 1,sortedTeams[i].getTeamName(),sortedTeams[i].getPoints(), sortedTeams[i].getGoalDiff());
        }
        return output;
    }

    // sorts teams based on placement in league 
    // helper method for endSeason() and createTable()
    // sorting logic details below method
    public Team[] sortTeams(){

        //Team[] unsortedTeams = getTeams();
        Team[] sortedTeams = getTeams();

        // sorting algorithm
        
        for (int i = 0; i < getNumTeams(); i++){
            int max = 0;
            int maxIndex = 0;
            for (int j = i; j < getNumTeams(); j ++){
                if (sortedTeams[j] == null){
                    continue;
                }
                if ((sortedTeams[j].getPoints() * 10000) + sortedTeams[j].getGoalDiff() + 100000 > max){
                    max = (sortedTeams[j].getPoints() * 10000) + sortedTeams[j].getGoalDiff() + 100000;
                    maxIndex = j;
                }
            }
            // swap logic
            Team temp = sortedTeams[maxIndex];
            sortedTeams[maxIndex] = sortedTeams[i];
            sortedTeams[i] = temp;

        }
        return sortedTeams;
    }

    /*
    Weighted Sum (WS):
        * This sort method uses a selection sort and a weighted sum model to determine table placement in league
        * weighted sum: multiplies the points by 10,000 and adds the goal difference to that, then adds a constant of 100,000 
        * weighted sum model allow the point count to take absolute priority and the goal difference to only act as a tie breaker
        * 100,000 constant used to prevent negative weighted sums that may be caused by 0pts and a goal difference less than 0
     
    Sorting Algorithm:
        * Selection sort was used to sort the teams based on their WS
        * Finds the greatest WS in the sort space of i to numTeams - 1
        * Greatest value and the value in the ith position are then swapped
        * Iteratively sorts in this way until the end of the array is reached
     */

}
public class YouthLeague extends League{

    private String ageRange;
    public YouthLeague(int matchLength, String matchDay, int goalArea, String leagueName,String ageRange){
        super(matchLength, matchDay, goalArea, leagueName);
        setAgeRange(ageRange);

    }

    public String getAgeRange() {return this.ageRange;}

    public void setAgeRange(String ageRange){
        if (ageRange == null || ageRange.length() < 1)
            throw new IllegalArgumentException("Must enter an age range");
        this.ageRange = ageRange;  
    }

    @Override
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
            if (team.getTeamId().charAt(0) != ('Y')){
                throw new IllegalArgumentException("Not a youth team ID\nFormat must be: Yxxxx");
            }
            try {
                Integer.parseInt(team.getTeamId().substring(1));
            } catch (Exception e) {
                throw new IllegalArgumentException("Team cannot join league");
            }
    
            // add team to array and increments numTeams variable
            this.teams[getNumTeams()] = team;
            setNumTeams(getNumTeams() + 1);
    
       
    }
    
}

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Vote {
    private String voteID;
    private String question;
    private String ownerUsername;
    private LocalDateTime voteDate;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private int option1Count;
    private int option2Count;
    private int option3Count;
    private int option4Count;

    public Vote(String ownerUsername,String question ,LocalDateTime voteDate, String option1, String option2, String option3, String option4) {
        voteID=UUID.randomUUID().toString();
        this.question=question;
        this.ownerUsername = ownerUsername;
        this.voteDate = voteDate;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        option1Count=0;
        option2Count=0;
        option3Count=0;
        option4Count=0;
    }
    public Vote(String ownerUsername,String question ,LocalDateTime voteDate, String option1, String option2, String option3, String option4,int option1Count,int option2Count,int option3Count,int option4Count) {
        voteID=UUID.randomUUID().toString();
        this.question=question;
        this.ownerUsername = ownerUsername;
        this.voteDate = voteDate;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.option1Count=option1Count;
        this.option2Count=option2Count;
        this.option3Count=option3Count;
        this.option4Count=option4Count;
    }

    public String getVoteID() {
        return voteID;
    }

    public String getQuestion() {
        return question;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public LocalDateTime getVoteDate() {
        return voteDate;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption1Count(int option1Count) {
        this.option1Count = option1Count;
    }

    public void setOption2Count(int option2Count) {
        this.option2Count = option2Count;
    }

    public void setOption3Count(int option3Count) {
        this.option3Count = option3Count;
    }

    public void setOption4Count(int option4Count) {
        this.option4Count = option4Count;
    }

    public int getOption1Count() {
        return option1Count;
    }

    public int getOption2Count() {
        return option2Count;
    }

    public int getOption3Count() {
        return option3Count;
    }

    public int getOption4Count() {
        return option4Count;
    }

}

package itm.capstone.skychat;

/**
 * Created by User on 2016-05-28.
 */
public class ChattingRoom {

    private String channel_id;
    private String channel_name;
    private String program_name;
    private String program_category;
    private String program_stime;
    private String program_etime;
    private String program_cast = "";
    private String program_summary = "";

    public ChattingRoom(String channel_id, String channel_name, String program_name, String program_category, String program_stime, String program_etime) {
        this.channel_id = channel_id;
        this.channel_name = channel_name;
        this.program_name = program_name;
        this.program_category = program_category;
        this.program_stime = program_stime;
        this.program_etime = program_etime;
    }


    public ChattingRoom(String program_summary, String program_etime, String program_stime, String program_category, String program_name, String channel_name, String channel_id) {
        this.program_summary = program_summary;
        this.program_etime = program_etime;
        this.program_stime = program_stime;
        this.program_category = program_category;
        this.program_name = program_name;
        this.channel_name = channel_name;
        this.channel_id = channel_id;
    }

    public ChattingRoom(String channel_id, String channel_name, String program_name, String program_category, String program_stime, String program_etime, String program_cast, String program_summary) {
        this.channel_id = channel_id;
        this.channel_name = channel_name;
        this.program_name = program_name;
        this.program_category = program_category;
        this.program_stime = program_stime;
        this.program_etime = program_etime;
        this.program_cast = program_cast;
        this.program_summary = program_summary;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public String getProgram_name() {
        return program_name;
    }

    public String getProgram_cast() {
        return program_cast;
    }

    public String getProgram_category() {
        return program_category;
    }

    public String getProgram_etime() {
        return program_etime;
    }

    public String getProgram_stime() {
        return program_stime;
    }

    public String getProgram_summary() {
        return program_summary;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public void setProgram_cast(String program_cast) {
        this.program_cast = program_cast;
    }

    public void setProgram_category(String program_category) {
        this.program_category = program_category;
    }

    public void setProgram_etime(String program_etime) {
        this.program_etime = program_etime;
    }

    public void setProgram_name(String program_name) {
        this.program_name = program_name;
    }

    public void setProgram_stime(String program_stime) {
        this.program_stime = program_stime;
    }

    public void setProgram_summary(String program_summary) {
        this.program_summary = program_summary;
    }
}

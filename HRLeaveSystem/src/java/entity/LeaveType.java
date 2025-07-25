package entity;

public class LeaveType {
    private int leaveTypeId;
    private String typeName;

    public LeaveType() {
    }

    public LeaveType(int leaveTypeId, String typeName) {
        this.leaveTypeId = leaveTypeId;
        this.typeName = typeName;
    }

    public int getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(int leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}

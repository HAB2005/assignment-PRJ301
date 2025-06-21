package entity;

public class Department {
    private int departmentId;
    private String departmentName;
    private Integer headOfDepartment;

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getHeadOfDepartment() {
        return headOfDepartment;
    }

    public void setHeadOfDepartment(Integer headOfDepartment) {
        this.headOfDepartment = headOfDepartment;
    }
}

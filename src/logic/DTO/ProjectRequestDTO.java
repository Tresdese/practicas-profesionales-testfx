package logic.DTO;

public class ProjectRequestDTO {
    private int requestId;
    private String tuition;
    private String organizationId;
    private String representativeId;
    private String projectName;
    private String description;
    private String generalObjective;
    private String immediateObjectives;
    private String mediateObjectives;
    private String methodology;
    private String resources;
    private String activities;
    private String responsibilities;
    private int duration;
    private String scheduleDays;
    private int directUsers;
    private int indirectUsers;
    private ProjectStatus status;
    private String requestDate;

    public ProjectRequestDTO() {
        this.requestId = 0;
        this.tuition = "";
        this.organizationId = "";
        this.representativeId = "";
        this.projectName = "";
        this.description = "";
        this.generalObjective = "";
        this.immediateObjectives = "";
        this.mediateObjectives = "";
        this.methodology = "";
        this.resources = "";
        this.activities = "";
        this.responsibilities = "";
        this.duration = 0;
        this.scheduleDays = "";
        this.directUsers = 0;
        this.indirectUsers = 0;
        this.status = ProjectStatus.pendiente;
        this.requestDate = "";
    }

    public ProjectRequestDTO(
            int requestId,
            String tuition,
            String organizationId,
            String representativeId,
            String projectName, // Cambiado aquí
            String description,
            String generalObjective,
            String immediateObjectives,
            String mediateObjectives,
            String methodology,
            String resources,
            String activities,
            String responsibilities,
            int duration,
            String scheduleDays,
            int directUsers,
            int indirectUsers,
            String status,
            String requestDate
    ) {
        this.requestId = requestId;
        this.tuition = tuition;
        this.organizationId = organizationId;
        this.representativeId = representativeId;
        this.projectName = projectName;
        this.description = description;
        this.generalObjective = generalObjective;
        this.immediateObjectives = immediateObjectives;
        this.mediateObjectives = mediateObjectives;
        this.methodology = methodology;
        this.resources = resources;
        this.activities = activities;
        this.responsibilities = responsibilities;
        this.duration = duration;
        this.scheduleDays = scheduleDays;
        this.directUsers = directUsers;
        this.indirectUsers = indirectUsers;
        this.status = ProjectStatus.valueOf(status);
        this.requestDate = requestDate;
    }

    // Getters y setters
    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public String getTuition() { return tuition; }
    public void setTuition(String tuition) { this.tuition = tuition; }

    public String getOrganizationId() { return organizationId; }
    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }

    public String getRepresentativeId() { return representativeId; }
    public void setRepresentativeId(String representativeId) { this.representativeId = representativeId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGeneralObjective() { return generalObjective; }
    public void setGeneralObjective(String generalObjective) { this.generalObjective = generalObjective; }

    public String getImmediateObjectives() { return immediateObjectives; }
    public void setImmediateObjectives(String immediateObjectives) { this.immediateObjectives = immediateObjectives; }

    public String getMediateObjectives() { return mediateObjectives; }
    public void setMediateObjectives(String mediateObjectives) { this.mediateObjectives = mediateObjectives; }

    public String getMethodology() { return methodology; }
    public void setMethodology(String methodology) { this.methodology = methodology; }

    public String getResources() { return resources; }
    public void setResources(String resources) { this.resources = resources; }

    public String getActivities() { return activities; }
    public void setActivities(String activities) { this.activities = activities; }

    public String getResponsibilities() { return responsibilities; }
    public void setResponsibilities(String responsibilities) { this.responsibilities = responsibilities; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getScheduleDays() { return scheduleDays; }
    public void setScheduleDays(String scheduleDays) { this.scheduleDays = scheduleDays; }

    public int getDirectUsers() { return directUsers; }
    public void setDirectUsers(int directUsers) { this.directUsers = directUsers; }

    public int getIndirectUsers() { return indirectUsers; }
    public void setIndirectUsers(int indirectUsers) { this.indirectUsers = indirectUsers; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    public String getRequestDate() { return requestDate; }
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }
}
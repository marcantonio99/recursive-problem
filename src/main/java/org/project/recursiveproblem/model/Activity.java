package org.project.recursiveproblem.model;

import jakarta.persistence.*;

@Entity
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String alias;
    @Enumerated(EnumType.STRING)
    private WorkStatus worked; // Lo stato del lavoro dell'attività, può essere YES o NO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_activity_id")
    private Activity parentActivity; // Riferimento all'attività padre (attività di livello superiore)

    // getter e setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public WorkStatus getWorked() {
        return worked;
    }

    public void setWorked(WorkStatus worked) {
        this.worked = worked;
    }

    public Activity getParentActivity() {
        return parentActivity;
    }

    public void setParentActivity(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    // Enumerazione per lo stato del lavoro
    public enum WorkStatus{
        YES, NO
    }
}

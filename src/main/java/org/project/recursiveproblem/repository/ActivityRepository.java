package org.project.recursiveproblem.repository;

import org.project.recursiveproblem.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // Query personalizzata per trovare tutte le attività in base allo stato di lavoro dell'attività e dello stato di lavoro dell'attività genitore
    // Trova tutte le attività figlie di un'attività padre con un dato stato di lavoro
    List<Activity> findAllByParentActivityAndWorked(Activity parentActivity, Activity.WorkStatus worked);

    // Trova tutte le attività figlie di un'attività padre con un dato stato di lavoro
    List<Activity> findAllByParentActivityAndWorkedAndParentActivity_Worked(Activity parentActivity, Activity.WorkStatus worked, Activity.WorkStatus parentWorked);
}

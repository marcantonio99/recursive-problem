package org.project.recursiveproblem.repository;

import org.project.recursiveproblem.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // Query personalizzata per trovare tutte le attività in base allo stato di lavoro dell'attività e dello stato di lavoro dell'attività genitore
    List<Activity> findAllByWorkedAndParentActivity_Worked(Activity.WorkStatus worked, Activity.WorkStatus parentWorked);
}

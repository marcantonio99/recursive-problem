package org.project.recursiveproblem.controller;

import org.project.recursiveproblem.model.Activity;
import org.project.recursiveproblem.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    @Autowired
    private ActivityRepository activityRepository;

    // Endpoint per segnare l'attività come completata
    @PostMapping("/{activityId}/work")
    public ResponseEntity<String> workOnActivity(@PathVariable Long activityId){
        // Trova l'attività tramite l'ID
        Activity activity = activityRepository.getById(activityId);
        // Se l'attività non ha un'attività padre, segnala direttamente come completata
        if (activity.getParentActivity() == null){
            activity.setWorked(Activity.WorkStatus.YES);
            activityRepository.save(activity);
        // Altrimenti, controlla se ci sono attività genitori non completate e richiama la funzione di verifica
        } else {

            List<Activity> noWorkedParentActivities = activityRepository.findAllByWorkedAndParentActivity_Worked(Activity.WorkStatus.NO, Activity.WorkStatus.YES);
            if (noWorkedParentActivities.isEmpty()){
                checkWorkStatus(activity.getParentActivity());
            }
        }
        return ResponseEntity.ok("Lavoro completato con successo");
    }

    // Funzione ricorsiva per verificare lo stato del lavoro dell'attività padre
    private void checkWorkStatus(Activity activity){
        if (activity.getParentActivity() != null){
            List<Activity> noWorkedParentActivities = activityRepository.findAllByWorkedAndParentActivity_Worked(Activity.WorkStatus.NO, Activity.WorkStatus.YES);
            if (noWorkedParentActivities.isEmpty()){
                activity.setWorked(Activity.WorkStatus.YES);
                activityRepository.save(activity);
                checkWorkStatus(activity.getParentActivity());
            }
        }
    }
}

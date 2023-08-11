package org.project.recursiveproblem.controller;

import org.project.recursiveproblem.model.Activity;
import org.project.recursiveproblem.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    @Autowired
    private ActivityRepository activityRepository;

    // Restituisce i dettagli di un'attività tramite il suo ID
    @GetMapping("/{activityId}")
    public ResponseEntity<Activity> getActivity(@PathVariable Long activityId){
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        return activityOptional.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Endpoint per segnare l'attività come completata
    @PostMapping("/{activityId}/work")
    public ResponseEntity<String> workOnActivity(@PathVariable Long activityId){
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Activity activity = activityOptional.get();

        // Verifico se l'attività è già stata completata
        if (activity.getWorked() == Activity.WorkStatus.YES){
            return ResponseEntity.badRequest().body("L'attività è già stata completata");
        }
        // Verifico se l'attività può essere lavorata
        if (!canWork(activity)){
            return ResponseEntity.badRequest().body("Impossibile lavorare questa attività");
        }

        // Imposto lo stato di lavoro dell'attività come completato e salvo
        activity.setWorked(Activity.WorkStatus.YES);
        activityRepository.save(activity);

        return ResponseEntity.ok("Lavoro completato con successo");
    }

        // Verifica se l'attività può essere lavorata considerando lo stato dei genitori
        private boolean canWork(Activity activity) {
            if (activity.getParentActivity() == null) {
                return true;  // L'attività non ha attività genitore, può essere lavorata
            }

            // Verifica se l'attività è una foglia, in tal caso può essere lavorata
            if (isLeafActivity(activity)) {
                return true;
            }

            // Verifica lo stato delle attività genitore e padri dei genitori
            List<Activity> parentActivities = activityRepository.findAllByParentActivityAndParentActivity_Worked(activity.getParentActivity(), Activity.WorkStatus.YES);
            for (Activity parentActivity : parentActivities) {
                if (parentActivity.getWorked() == Activity.WorkStatus.NO) {
                    return false;  // Un attività genitore non è stata ancora lavorata
                }
            }

            return canWork(activity.getParentActivity());  // Ricorsione per i genitori
        }

        // Verifica se l'attività è una foglia (non ha attività figlie)
        private boolean isLeafActivity(Activity activity) {
            List<Activity> childActivities = activityRepository.findAllByParentActivityAndParentActivity_Worked(activity, Activity.WorkStatus.YES);
            return childActivities.isEmpty();
        }
}

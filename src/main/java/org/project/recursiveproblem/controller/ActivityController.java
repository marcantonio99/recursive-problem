package org.project.recursiveproblem.controller;

import org.project.recursiveproblem.model.Activity;
import org.project.recursiveproblem.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Activity> getActivity(@PathVariable Long activityId) {
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        return activityOptional.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Endpoint per segnare l'attività come completata
    @PostMapping("/{activityId}/work")
    public ResponseEntity<String> workOnActivity(@PathVariable Long activityId) {
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isEmpty()) {
            // Se l'attività non è trovata, restituisce 404 Not Found
            return ResponseEntity.notFound().build();
        }

        Activity activity = activityOptional.get();

        // Se l'attività è già stata completata
        if (activity.getWorked() == Activity.WorkStatus.YES) {
            return ResponseEntity.badRequest().body("L'attività è già stata completata");
        }

        // Se l'attività non può essere lavorata
        if (!canWork(activity)) {
            return ResponseEntity.badRequest().body("Impossibile lavorare questa attività");
        }

        // Imposta lo stato di lavoro dell'attività come completato e salva le modifiche nel repository
        activity.setWorked(Activity.WorkStatus.YES);
        activityRepository.save(activity);

        return ResponseEntity.ok("Lavoro completato con successo");
    }

    // Verifica se l'attività può essere lavorata considerando lo stato dei genitori
    private boolean canWork(Activity activity) {

        // Se l'attività non ha un'attività genitore, può essere lavorata
        if (activity.getParentActivity() == null) {
            return true;
        }

        // Se l'attività genitore non è stata lavorata, non è possibile lavorare l'attività corrente
        if (activity.getParentActivity().getWorked() == Activity.WorkStatus.NO) {
            return false;
        }

        // Verifica se tutte le attività genitore analoghe sono state lavorate
        if (!areAnalogousActivitiesWorked(activity.getParentActivity())) {
            return false;
        }

        // Ricorsione per i genitori: controlla l'attività genitore
        return canWork(activity.getParentActivity());
    }

    // Verifica se tutte le attività genitore analoghe sono state lavorate
    private boolean areAnalogousActivitiesWorked(Activity activity) {
        // Trova tutte le attività genitore analoghe che sono state lavorate
        List<Activity> analogousActivities = activityRepository.findAllByParentActivityAndWorked(activity.getParentActivity(), Activity.WorkStatus.YES);

        // Controlla se almeno una attività genitore analoga non è stata lavorata
        for (Activity analogousActivity : analogousActivities) {
            if (analogousActivity.getId() != activity.getId() && analogousActivity.getWorked() == Activity.WorkStatus.NO) {
                return false;
            }
        }

        // Tutte le attività genitore analoghe sono state lavorate
        return true;
    }
}

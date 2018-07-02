package de.symeda.sormas.app.contact.read.sub;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.tagview.Tag;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactReadSymptomsInfoLayoutBinding;
import de.symeda.sormas.app.shared.VisitFormNavigationCapsule;
import de.symeda.sormas.app.symptom.Symptom;

/**
 * Created by Orson on 02/01/2018.
 */

public class VisitReadSymptomsFragment extends BaseReadActivityFragment<FragmentContactReadSymptomsInfoLayoutBinding, Symptoms, Visit> {

    private String recordUuid;
    private VisitStatus pageStatus;
    private Symptoms record;

    private List<Tag> yesResult;
    private List<Tag> noResult;
    private List<Tag> unknownResult;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        pageStatus = (VisitStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Symptoms _symptom = null;
            Visit visit = getActivityRootData();
            List<Symptom> sList = new ArrayList<>();

            if (visit != null) {
                if (visit.isUnreadOrChildUnread())
                    DatabaseHelper.getVisitDao().markAsRead(visit);

                _symptom = visit.getSymptoms();
                //_symptom = DatabaseHelper.getSymptomsDao().queryUuid(visit.getSymptoms().getUuid());

                sList = Symptom.makeSymptoms(visit.getDisease()).loadState(visit.getSymptoms());
            }

            resultHolder.forItem().add(visit);
            resultHolder.forItem().add(_symptom); //TODO: Do we need this

            resultHolder.forOther().add(getSymptomsYes(sList));
            resultHolder.forOther().add(getSymptomsNo(sList));
            resultHolder.forOther().add(getSymptomsUnknown(sList));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            if (itemIterator.hasNext())
                record = itemIterator.next();

            //TODO: Do we need this
            /*if (itemIterator.hasNext())
                symptom = itemIterator.next();*/

            if (otherIterator.hasNext())
                yesResult =  otherIterator.next();

            if (otherIterator.hasNext())
                noResult =  otherIterator.next();

            if (otherIterator.hasNext())
                unknownResult =  otherIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentContactReadSymptomsInfoLayoutBinding contentBinding) {
        if (record != null) {
            contentBinding.setData(record);

            contentBinding.setSymptomsYes(yesResult);
            contentBinding.setSymptomsUnknown(unknownResult);
            contentBinding.setSymptomsNo(noResult);
        }
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactReadSymptomsInfoLayoutBinding contentBinding) {

    }

    @Override
    protected void updateUI(FragmentContactReadSymptomsInfoLayoutBinding contentBinding, Symptoms symptoms) {

    }

    @Override
    public void onPageResume(FragmentContactReadSymptomsInfoLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;
        record = getActivityRootData().getSymptoms();
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_symptom_information);
    }

    @Override
    public Symptoms getPrimaryData() {
        return record;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_contact_read_symptoms_info_layout;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public static VisitReadSymptomsFragment newInstance(VisitFormNavigationCapsule capsule, Visit activityRootData) {
        return newInstance(VisitReadSymptomsFragment.class, capsule, activityRootData);
    }

    private List<Tag> getSymptomsYes(List<Symptom> list) {
        List<Tag> results = new ArrayList();
        for (Symptom s: list) {
            if (s.getState() == SymptomState.YES) {
                results.add(new Tag(s.getName()));
            }
        }

        return results;
    }

    private List<Tag> getSymptomsUnknown(List<Symptom> list) {
        List<Tag> results = new ArrayList();
        for (Symptom s: list) {
            if (s.getState() == SymptomState.UNKNOWN) {
                results.add(new Tag(s.getName()));
            }
        }

        return results;
    }

    private List<Tag> getSymptomsNo(List<Symptom> list) {
        List<Tag> results = new ArrayList();
        for (Symptom s: list) {
            if (s.getState() == SymptomState.NO) {
                results.add(new Tag(s.getName()));
            }
        }

        return results;
    }
}
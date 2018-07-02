package de.symeda.sormas.app.contact.read;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactReadPersonInfoLayoutBinding;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;

/**
 * Created by Orson on 01/01/2018.
 */

public class ContactReadPersonFragment  extends BaseReadActivityFragment<FragmentContactReadPersonInfoLayoutBinding, Person, Contact> {

    private AsyncTask onResumeTask;
    private String recordUuid;
    private FollowUpStatus followUpStatus;
    private ContactClassification contactClassification = null;
    private Person record;
    //private Person person;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveFilterStatusState(outState, followUpStatus);
        savePageStatusState(outState, contactClassification);
        saveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        followUpStatus = (FollowUpStatus) getFilterStatusArg(arguments);
        contactClassification = (ContactClassification) getPageStatusArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Person p = DatabaseHelper.getPersonDao().build();
            Contact contact = getActivityRootData();

            if (contact != null) {
                if (contact.isUnreadOrChildUnread())
                    DatabaseHelper.getContactDao().markAsRead(contact);

                p = DatabaseHelper.getPersonDao().queryUuid(contact.getPerson().getUuid());
            }

            //resultHolder.forItem().add(contact);
            resultHolder.forItem().add(p);
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record =  itemIterator.next();

            if (record == null)
                getActivity().finish();

            /*if (itemIterator.hasNext())
                person =  itemIterator.next();*/
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentContactReadPersonInfoLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactReadPersonInfoLayoutBinding contentBinding) {
    }

    @Override
    protected void updateUI(FragmentContactReadPersonInfoLayoutBinding contentBinding, Person person) {

    }

    @Override
    public void onPageResume(FragmentContactReadPersonInfoLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

        try {
            DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {
                @Override
                public void onPreExecute() {
                    //getBaseActivity().showPreloader();
                    //
                }

                @Override
                public void doInBackground(TaskResultHolder resultHolder) {
                    Person p = DatabaseHelper.getPersonDao().build();
                    Contact contact = getActivityRootData();

                    if (contact != null) {
                        if (contact.isUnreadOrChildUnread())
                            DatabaseHelper.getContactDao().markAsRead(contact);

                        p = DatabaseHelper.getPersonDao().queryUuid(contact.getPerson().getUuid());
                    }

                    //resultHolder.forItem().add(contact);
                    resultHolder.forItem().add(p);
                }
            };
            onResumeTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getBaseActivity().hidePreloader();
                    //getBaseActivity().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                    if (itemIterator.hasNext())
                        record = itemIterator.next();

                    /*if (itemIterator.hasNext())
                        person = itemIterator.next();*/

                    if (record != null)
                        requestLayoutRebind();
                    else {
                        getActivity().finish();
                    }
                }
            });
        } catch (Exception ex) {
            //getBaseActivity().hidePreloader();
            //getBaseActivity().showFragmentView();
        }

    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_person_information);
    }

    @Override
    public Person getPrimaryData() {
        return record;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_contact_read_person_info_layout;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public static ContactReadPersonFragment newInstance(ContactFormNavigationCapsule capsule, Contact activityRootData) {
        return newInstance(ContactReadPersonFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}

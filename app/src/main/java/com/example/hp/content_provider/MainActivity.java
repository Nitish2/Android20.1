package com.example.hp.content_provider;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;
    TextView textView;
    // Adding elements into string arrays
    private String[] name = {"Mohit", "Karan", "Varun", "Anirudh"};
    private String[] number = {"8765992910", "9112211339", "7924672890", "9868208909"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        // Checking Permission
        if (ContextCompat.checkSelfPermission(this, // if Statement
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

            } else {

                // Making request for permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);


            }
        }
    }
    //onRequestPermissionsResult() is a callback for the result from requesting permissions.
    //  This method is invoked for every call on requestPermissions
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If the request is cancelled, the resulted arrays will be empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    /*
                     Permission was granted perform the functionality that depends
                      upon the permission.
                      */
                    for (int i = 0; i < name.length; i++) {
                        writeContact(name[i], number[i]);
                        textView.setText("Contacts are Saved");
                    }
                } else {

                    /*
                    if permission denied the functionality that depends on this permission
                    will be disable.
                     */

                }
                return;
            }

        }
    }
    // Creating method for inserting number , name and contact details internally .
    private void writeContact(String name, String number) {
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
        //Inserting raw contact using RawContacts.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert
                (ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
        //Inserting name using Data.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert
                (ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name).build());
        //Inserting number using Data.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
        try {
            getApplicationContext().getContentResolver().
                    applyBatch(ContactsContract.AUTHORITY, contentProviderOperations);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }
    //Creating onDestroy() method
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;

    }
}
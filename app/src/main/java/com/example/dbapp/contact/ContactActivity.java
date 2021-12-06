package com.example.dbapp.contact;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dbapp.R;

import java.util.ArrayList;
import java.util.List;


public class ContactActivity extends AppCompatActivity {

    TabHost tabHost;
    EditText nameText, phoneText, emailText, addressText;
    Button addContactBtn;
    Button deleteAllBtn;

    List<Contact> contacts = new ArrayList<>();
    ListView contactListView;

    ImageView contactImageImgView;

    DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        initTabHost();

        nameText = (EditText) findViewById(R.id.textName);
        phoneText = (EditText) findViewById(R.id.textPhone);
        emailText = (EditText) findViewById(R.id.textEmail);
        addressText = (EditText) findViewById(R.id.textAddress);
        contactListView = (ListView) findViewById(R.id.listView);
        contactImageImgView = (ImageView) findViewById(R.id.imgViewContactImage);
        dbHandler = new DatabaseHandler(getApplicationContext());
        deleteAllBtn = (Button) findViewById(R.id.deleteAllBtn);
        addContactBtn = (Button) findViewById(R.id.btnAddContact);

        addContactBtn.setOnClickListener(view -> {
            Contact contact = new Contact(dbHandler.getContactsCount(),   String.valueOf(nameText.getText()),String.valueOf(phoneText.getText()), String.valueOf(addressText.getText()),  String.valueOf(emailText.getText()));
            dbHandler.createContact(contact);
            contacts.add(contact);
            populateList();
            tabHost.setCurrentTab(1);
            Toast.makeText(getApplicationContext(), nameText.getText().toString() + " has been added to your contacts", Toast.LENGTH_SHORT).show();
            nameText.setText("");
            phoneText.setText("");
            emailText.setText("");
            addressText.setText("");
        });

        deleteAllBtn.setOnClickListener(view ->{
            AlertDialog.Builder alert = new AlertDialog.Builder( view.getContext());
            alert.setTitle("Are you really want to delete all contacts");
            // alert.setMessage("Message");

            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dbHandler.deleteAllContacts();
                    contacts.clear();
                    Toast.makeText(getApplicationContext(),  " All contacts has been deleted", Toast.LENGTH_SHORT).show();
                    populateList();
                }
            });

            alert.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });

            alert.show();

        });

        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                addContactBtn.setEnabled(!nameText.getText().toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        List<Contact> addableContacts = dbHandler.getAllContacts();
        int contactCount = dbHandler.getContactsCount();

        for (int i = 0; i < contactCount; i++){
            contacts.add(addableContacts.get(i));
        }

        if (!addableContacts.isEmpty()){
            populateList();
        }
    }

    private void initTabHost() {
        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("creator");
        tabSpec.setContent(R.id.tabCreator);
        tabSpec.setIndicator("Create New");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("list");
        tabSpec.setContent(R.id.tabContactList);
        tabSpec.setIndicator("List");
        tabHost.addTab(tabSpec);
    }

    private void populateList(){
        ArrayAdapter<Contact> adapter = new ContactListAdapter();
        contactListView.setAdapter(adapter);
    }


    private class ContactListAdapter extends ArrayAdapter<Contact> {
        public ContactListAdapter(){
            super(ContactActivity.this, R.layout.contact_item, contacts);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent){
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.contact_item, parent, false);


            Contact currentContact = contacts.get(position);
            TextView name = (TextView) view.findViewById(R.id.textContactName);
            name.setText(currentContact.getName());

            TextView phone = (TextView) view.findViewById(R.id.textContactPhone);
            phone.setText(currentContact.getPhone());

            TextView email = (TextView)  view.findViewById(R.id.textContactEmail);
            email.setText(currentContact.getEmail());

            TextView address = (TextView) view.findViewById(R.id.textContactAddress);
            address.setText(currentContact.getAddress());

            ImageView ivContactImage = (ImageView) view.findViewById(R.id.ivContactImage);
//            ivContactImage.setImageURI(currentContact.getImageURI());
            ivContactImage.setImageDrawable(getResources().getDrawable(R.drawable.user));

            View deleteBtn = view.findViewById(R.id.deleteBtn);
            deleteBtn.setOnClickListener(view1 -> {
                AlertDialog.Builder alert = new AlertDialog.Builder( view1.getContext());
                alert.setTitle("Are you really want to delete "+ currentContact.getName());
                // alert.setMessage("Message");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dbHandler.deleteContact(currentContact);
                        contacts.remove(position);
                        populateList();
                        Toast.makeText(getApplicationContext(), "Contact has been added from your contacts", Toast.LENGTH_SHORT).show();
                    }
                });

                alert.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });

                alert.show();

            });

            View shareBtn = view.findViewById(R.id.shareBtn);
            shareBtn.setOnClickListener((viewClick) ->{
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact "+ currentContact.getName());
                sendIntent.putExtra(Intent.EXTRA_TEXT, currentContact.getEmail() + " " + currentContact.getPhone() + " " + currentContact.getAddress());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            });


            View editBtn = view.findViewById(R.id.editBtn);
            editBtn.setOnClickListener((viewClick) -> {
                if (currentContact == null) return;

                AlertDialog.Builder builder = new AlertDialog.Builder(viewClick.getContext());
                ViewGroup viewGroup = viewClick.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(viewClick.getContext())
                        .inflate(R.layout.dialog_edit, viewGroup, false);

                EditText textName = dialogView.findViewById(R.id.dialogTextName);
                textName.setText(currentContact.getName());

                EditText textPhone = dialogView.findViewById(R.id.dialogTextPhone);
                textPhone.setText(currentContact.getPhone());

                EditText textAddress = dialogView.findViewById(R.id.dialogTextAddress);
                textAddress.setText(currentContact.getAddress());

                EditText textEmail = dialogView.findViewById(R.id.dialogTextEmail);
                textEmail.setText(currentContact.getEmail());

                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                dialogView.findViewById(R.id.btnCancel).setOnClickListener((viewOk) -> alertDialog.dismiss());

                dialogView.findViewById(R.id.btnUpdateContact).setOnClickListener((viewOk) ->{
                    currentContact.setName(textName.getText().toString());
                    currentContact.setAddress(textAddress.getText().toString());
                    currentContact.setPhone(textPhone.getText().toString());
                    currentContact.setEmail(textEmail.getText().toString());
                    dbHandler.updateContact(currentContact);
                    populateList();
                    alertDialog.dismiss();
                });
            });

            return view;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

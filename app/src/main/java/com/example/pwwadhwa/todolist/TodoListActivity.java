package com.example.pwwadhwa.todolist;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TodoListActivity extends AppCompatActivity {

    /**
     * Holds the list of to-do items.
     */
    ArrayList<String> itemList;

    /**
     * Adapter for the list of to-do items.
     */
    ArrayAdapter<String> itemAdapter;

    /**
     * ListView that shows the to-do items.
     */
    ListView listView;

    /**
     * TextView that is used to enter to-do items.
     */
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the activity and actiokn bar.
        setContentView(R.layout.activity_todo_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Read the to-do items into the list.
        itemList = new ArrayList<String>();
        readItems();
        itemAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemList);
        listView = (ListView) findViewById(R.id.lvItemList);
        listView.setAdapter(itemAdapter);

        // Locate the text-field.
        editText = (EditText) findViewById(R.id.editText);

        // Set up a long-press handler to remove items.
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                itemList.remove(i);
                itemAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }
        });

        // Set up a click listener to edit items.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                showUpdateBox(itemList.get(position), position);
            }
        });
    }

    /**
     * Reading item list from file saved as todo.txt
     */
    private void readItems() {
        File fileDir = getFilesDir();
        File file = new File(fileDir, "todo.txt");
        try {
            itemList = new ArrayList<String>(FileUtils.readLines(file));
        } catch (IOException e) {
            // TODO: Handle exceptions.
        }
    }

    /**
     * Writing items in file todo.txt
     */
    private void writeItems() {
        File fileDir = getFilesDir();
        File file = new File(fileDir, "todo.txt");
        try {
            FileUtils.writeLines(file, itemList);
        } catch (IOException e) {
            // TODO: Handle exceptions.
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is invoked when Submit button is clicked.
     * When new item is added it is added to listview and saved in todo.txt file.
     *
     * @param view
     */
    public void onSubmit(View view) {
        if (editText.getText().toString().isEmpty()) {
            return;
        }
        itemAdapter.add(editText.getText().toString());
        editText.setText("");
        writeItems();
    }

    /**
     * It shows an dialog box that has edittext to update the item and on clicking update button it saves changes
     *
     * @param oldItem  - Item that was clicked
     * @param position - Position of an item that was clicked.
     */
    public void showUpdateBox(String oldItem, final int position) {
        final Dialog dialog = new Dialog(TodoListActivity.this);
        dialog.setTitle("Update Item");
        dialog.setContentView(R.layout.edit_text);

        final EditText updateEditTxt = (EditText) dialog.findViewById(R.id.updateEditText);
        updateEditTxt.setText(oldItem);

        Button updateButton = (Button) dialog.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (updateEditTxt.getText().toString().isEmpty()) {
                    itemList.remove(position);
                } else {
                    itemList.set(position, updateEditTxt.getText().toString());
                }
                itemAdapter.notifyDataSetChanged();
                writeItems();
                dialog.dismiss();
            }
        });

        dialog.show();

        //Grab the window of the dialog, and change the width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());

        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }
}

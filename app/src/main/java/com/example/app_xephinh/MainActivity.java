package com.example.app_xephinh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static GestureDetectGridView mGridView;

    private static final int COLUMNS = 3;
    private static final int DIMENSIONS = COLUMNS * COLUMNS;

    private static int mColumnWidth, mColumnHeight;

    public static final String up = "up";
    public static final String down = "down";
    public static final String left = "left";
    public static final String right = "right";

    private static String[] tileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        scramble();

        setDimensions();
    }

    private void init() {
        mGridView = (GestureDetectGridView) findViewById(R.id.grid);
        mGridView.setNumColumns(COLUMNS);

        tileList = new String[DIMENSIONS];
        for (int i = 0; i < DIMENSIONS; i++) {
            tileList[i] = String.valueOf(i);
        }
    }

    private void scramble() {
        int index;
        String temp;
        Random random = new Random();

        for (int i = tileList.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = tileList[index];
            tileList[index] = tileList[i];
            tileList[i] = temp;
        }
    }

    private void setDimensions() {
        ViewTreeObserver vto = mGridView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int displayWidth = mGridView.getMeasuredWidth();
                int displayHeight = mGridView.getMeasuredHeight();

                int statusbarHeight = getStatusBarHeight(getApplicationContext());
                int requiredHeight = displayHeight - statusbarHeight;

                mColumnWidth = displayWidth / COLUMNS;
                mColumnHeight = requiredHeight / COLUMNS;

                display(getApplicationContext());
            }
        });
    }

    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");

        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    private static void display(Context context) {
        ArrayList<Button> buttons = new ArrayList<>();
        Button button;

        for (String s : tileList) {
            button = new Button(context);

            switch (s) {
                case "0":
                    button.setBackgroundResource(R.drawable.img_1);
                    break;
                case "1":
                    button.setBackgroundResource(R.drawable.img_2);
                    break;
                case "2":
                    button.setBackgroundResource(R.drawable.img_3);
                    break;
                case "3":
                    button.setBackgroundResource(R.drawable.img_4);
                    break;
                case "4":
                    button.setBackgroundResource(R.drawable.img_5);
                    break;
                case "5":
                    button.setBackgroundResource(R.drawable.img_6);
                    break;
                case "6":
                    button.setBackgroundResource(R.drawable.img_7);
                    break;
                case "7":
                    button.setBackgroundResource(R.drawable.img_8);
                    break;
                case "8":
                    button.setBackgroundResource(R.drawable.img_9);
                    break;
            }

            buttons.add(button);
        }

        mGridView.setAdapter(new CustomAdapter(buttons, mColumnWidth, mColumnHeight));
    }

    private static void swap(Context context, int currentPosition, int swap) {
        String newPosition = tileList[currentPosition + swap];
        tileList[currentPosition + swap] = tileList[currentPosition];
        tileList[currentPosition] = newPosition;
        display(context);

        if (isSolved()) Toast.makeText(context, "YOU WIN!", Toast.LENGTH_SHORT).show();
    }

    public static void moveTiles(Context context, String direction, int position) {

        // Upper-left-corner tile
        if (position == 0) {

            if (direction.equals(right)) swap(context, position, 1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Upper-center tiles
        } else if (position > 0 && position < COLUMNS - 1) {
            switch (direction) {
                case left:
                    swap(context, position, -1);
                    break;
                case down:
                    swap(context, position, COLUMNS);
                    break;
                case right:
                    swap(context, position, 1);
                    break;
                default:
                    Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();
                    break;
            }

            // Upper-right-corner tile
        } else if (position == COLUMNS - 1) {
            if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Left-side tiles
        } else if (position > COLUMNS - 1 && position < DIMENSIONS - COLUMNS &&
                position % COLUMNS == 0) {
            switch (direction) {
                case up:
                    swap(context, position, -COLUMNS);
                    break;
                case right:
                    swap(context, position, 1);
                    break;
                case down:
                    swap(context, position, COLUMNS);
                    break;
                default:
                    Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();
                    break;
            }

            // Right-side AND bottom-right-corner tiles
        } else if (position == COLUMNS * 2 - 1 || position == COLUMNS * 3 - 1) {
            switch (direction) {
                case up:
                    swap(context, position, -COLUMNS);
                    break;
                case left:
                    swap(context, position, -1);
                    break;
                case down:

                    // Tolerates only the right-side tiles to swap downwards as opposed to the bottom-
                    // right-corner tile.
                    if (position <= DIMENSIONS - COLUMNS - 1) swap(context, position,
                            COLUMNS);
                    else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();
                    break;
            }

            // Bottom-left corner tile
        } else if (position == DIMENSIONS - COLUMNS) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Bottom-center tiles
        } else if (position < DIMENSIONS - 1 && position > DIMENSIONS - COLUMNS) {
            switch (direction) {
                case up:
                    swap(context, position, -COLUMNS);
                    break;
                case left:
                    swap(context, position, -1);
                    break;
                case right:
                    swap(context, position, 1);
                    break;
                default:
                    Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();
                    break;
            }

            // Center tiles
        } else {
            switch (direction) {
                case up:
                    swap(context, position, -COLUMNS);
                    break;
                case left:
                    swap(context, position, -1);
                    break;
                case right:
                    swap(context, position, 1);
                    break;
                default:
                    swap(context, position, COLUMNS);
                    break;
            }
        }
    }

    private static boolean isSolved() {
        boolean solved = false;

        for (int i = 0; i < tileList.length; i++) {
            if (tileList[i].equals(String.valueOf(i))) {
                solved = true;
            } else {
                solved = false;
                break;
            }
        }

        return solved;
    }
}
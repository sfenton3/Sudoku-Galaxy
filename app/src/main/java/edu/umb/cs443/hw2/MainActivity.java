package edu.umb.cs443.hw2;
import android.content.DialogInterface;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


import static android.R.color.transparent;
import static android.R.id.text1;
import static edu.umb.cs443.hw2.R.color.colorAccent;
import static edu.umb.cs443.hw2.R.color.yellowtransparent;
import static edu.umb.cs443.hw2.R.color.colorPrimaryDark;
import static edu.umb.cs443.hw2.R.color.navyblue;
import static edu.umb.cs443.hw2.R.color.navyblue1;
import static edu.umb.cs443.hw2.R.color.win;
import static edu.umb.cs443.hw2.R.color.yellow;
import static edu.umb.cs443.hw2.R.color.yellowselecttransparent;
import static edu.umb.cs443.hw2.R.color.yellowselect;


/*
*  CREATED BY ABDELRAHMAN OBYAT AND SCOTT FENTON
*  CREATED FOR UMASS BOSTON COMPUTER SCIENCE 443
*/

public class MainActivity extends Activity {
    GridView gridView2;
    GridView gridView;
    private static int w = 9;
    private Random r = new Random();
    int position1;
    int previousposition1;
    int currentposiiton;
    int positionnumber = -1;
    private static int blankspace;
    int Do_the_alert = 0;

    static String[] numbers = new String[81];
    static String[] blanksBackup = new String[81];
    static String[] copynumbers = new String[81];
    static Integer[] numbervalues = new Integer[9];
    static String[] savenumbers= new String[81];
    static String[] defaultnumbers= new String[81];

    public static final MediaPlayer mp[] = new MediaPlayer[7];


    private int difVal = 1;
    private int TEST = 4;
    private int EASY = 3;
    private int MEDIUM = 2;
    private int HARD = 1;

    private int DIFFICULTY = TEST;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridView1);
        gridView2 = (GridView) findViewById(R.id.gridView2);

       Bundle extras = getIntent().getExtras();

        if(extras == null)
            ChooseDifficulty();

        if(extras != null)
            DIFFICULTY = extras.getInt("Difficulty");



        gridView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, numbers) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(text1);
                text.setTextSize(34);
                gridView.setBackgroundColor(getResources().getColor(colorPrimaryDark));
                return view;
            }
        });



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, numbers);


        int[][] Sudoku = Array2D();
        convertArray2dToGridview(Sudoku);
        easy();

        final MediaPlayer mp1 = MediaPlayer.create(this, R.raw.pencil2);
        final MediaPlayer mp2 = MediaPlayer.create(this, R.raw.pencil3);
        final MediaPlayer mp3 = MediaPlayer.create(this, R.raw.pencil4);
        final MediaPlayer mp4 = MediaPlayer.create(this, R.raw.pencil5);
        final MediaPlayer mp5 = MediaPlayer.create(this, R.raw.pencil6);
        final MediaPlayer mp6 = MediaPlayer.create(this, R.raw.pencil7);
        final MediaPlayer mp7 = MediaPlayer.create(this, R.raw.pencil8);


        mp[0] = mp1;
        mp[1] = mp2;
        mp[2] = mp3;
        mp[3] = mp4;
        mp[4] = mp5;
        mp[5] = mp6;
        mp[6] = mp7;

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                position1 = position;
                change();
                colorcell();
                revertpreviouscolorcell();
                revertpreviouscolorcell();
                highlightConflicts();
                testing();

                ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();


            }
        });
        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        ArrayAdapter<Integer> adapter2 = new ArrayAdapter<Integer>(this,
                R.layout.list_item2, numbervalues);
        gridView2.setAdapter(adapter2);

        change();
        gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent2, View v2,
                                    int position2, long id2) {
                Random rand = new Random();
                mp[rand.nextInt(6) + 0].start();

                positionnumber = position2;
                change();
                initalcolorcell();

                revertpreviouscolorcell();
                popup();
                revertpreviouscolorcell();
                testing();
                highlightConflicts();
            }

        });


        ((ArrayAdapter) gridView2.getAdapter()).notifyDataSetChanged();
        change();

    }



    private ArrayList<ArrayList<Integer>> array = new ArrayList<ArrayList<Integer>>();
    private Random rand = new Random();






    public int[][] Array2D() {
        int[][] Sudoku = new int[9][9];
        int value = 0;
        resetArray2D(Sudoku);
        while (value < 81) {
            if (array.get(value).size() != 0) {
                int i = rand.nextInt(array.get(value).size());
                int pos = array.get(value).get(i);
                if (!FindnextValues(Sudoku, value, pos)) {
                    int curx = value % 9;
                    int cury = value / 9;
                    Sudoku[curx][cury] = pos;
                    array.get(value).remove(i);
                    value++;
                } else {
                    array.get(value).remove(i);
                }
            } else {
                for (int i = 1; i <= 9; i++) {
                    array.get(value).add(i);
                }
                value--;
            }
        }
        return Sudoku;
    }

    private void resetArray2D(int[][] Sudoku) {
        array.clear();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Sudoku[x][y] = -1;
            }
        }
        for (int x = 0; x < 81; x++) {
            array.add(new ArrayList<Integer>());
            for (int i = 1; i <= 9; i++) {
                array.get(x).add(i);
            }
        }
    }

    private boolean FindnextValues(int[][] Sudoku, int value, final int pos) {

        int curx = value % 9;
        int cury = value / 9;

        if (Xvalues(Sudoku, curx, cury, pos)
                || Yvalues(Sudoku, curx, cury, pos)
                || Gridvalues(Sudoku, curx, cury, pos)) {

            return true;
        }
        return false;
    }

    private boolean Xvalues(final int[][] Sudoku, final int curx, final int cury, final int pos) {
        for (int x = curx - 1; x >= 0; x--) {
            if (pos == Sudoku[x][cury]) {
                return true;
            }
        }
        return false;
    }

    private boolean Yvalues(final int[][] Sudoku, final int curx, final int cury, final int pos) {
        for (int y = cury - 1; y >= 0; y--) {
            if (pos == Sudoku[curx][y]) {
                return true;
            }
        }
        return false;
    }

    private boolean Gridvalues(final int[][] Sudoku, final int curx, final int cury, final int pos) {
        int cubedx = curx / 3;
        int cubedy = cury / 3;
        for (int x = cubedx * 3; x < cubedx * 3 + 3; x++) {
            for (int y = cubedy * 3; y < cubedy * 3 + 3; y++) {
                if ((x != curx || y != cury) && pos == Sudoku[x][y]) {
                    return true;
                }
            }
        }
        return false;
    }

    private void convertArray2dToGridview(int Sudoku[][]) {

        List<Integer> list = new ArrayList<Integer>();

        TextView Cells = (TextView) findViewById(R.id.textCell);
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                for (int i = 0; i < Sudoku.length; i++) {
                    for (int j = 0; j < Sudoku[i].length; j++) {
                        list.add(Sudoku[i][j]);
                    }
                }
            }
        }

        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = String.valueOf(list.get(i));

        }
        //create a copy of the solved gridview
        copynumbers = numbers.clone();


    }

//THE ABOVE CODE ALL GENERATED 2D GRID AND CONVERTS IT TO 1D GRIDVIEW
    public Handler threadHandler = new Handler() {
        public void handleMessage(android.os.Message message) {
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        }

    };

    public void positionplace(View view) {
        popup();
    }

    public void coloreachcell(View view) {
        colorcell();
    }
    public void highlightallselected(View view){highlightrestofselected();}
    public void revertpreviouscolor() {
        revertpreviouscolorcell();
    }
    public void setcolor(){initalcolorcell();
    }

    public void easymode(View view) {

        easy();
    }
    public void checkconflict(View view){highlightConflicts();}
    public void resetactivity(View view) {
        reset();
    }

    public void checkpoint(View view){
        savestate();}
    public void restorepoint(View view){ loadstate();}

    public void easy() {

        int challengeNumber;

        if(DIFFICULTY == EASY)
            challengeNumber = 28;
        else if (DIFFICULTY == MEDIUM)
            challengeNumber = 52;
        else if (DIFFICULTY == TEST)
            challengeNumber = 1;
        else
            challengeNumber = 75;

        for (int i = 0; i < blanksBackup.length; i++) {
            blanksBackup[i] = "-1";
        }
        blankspace = 0;


        while (blankspace < challengeNumber) {

            for (int k = 0; k < challengeNumber; k++) {
                int xval = r.nextInt(9);
                int yval = r.nextInt(9);
                blanksBackup[k] = String.valueOf(xval * w + yval);
            }


            blankspace++;

        }


        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        //System.out.println("TESTING ARRAYblanksBackup" + Arrays.toString(blanksBackup));
     //   System.out.println("TESTING ARRAYnumbers" + Arrays.toString(numbers));

        int blankvalue=0;

        while (blankvalue<83) {
             String quotednumber = String.valueOf(blankvalue);
           // System.out.println("checkingARRAYnumber3: " + quotednumber);
            if (Arrays.asList(blanksBackup).contains(quotednumber)) {
              //  System.out.println("checkingARRAYvalue: " + Arrays.asList(blanksBackup).contains(quotednumber));
               // System.out.println("THISARRAYnumber3: " + quotednumber);
                numbers[blankvalue]=" ";
            }
            defaultnumbers=numbers.clone();
            savenumbers = numbers.clone();
            blankvalue++;
            }
    }


    public void clicked(View view) {change();}

//the second gridview of numbers
    void change() {


        for (int newv = 0; newv < numbervalues.length; newv++) {
            numbervalues[newv] = newv + 1;
        }
        ((ArrayAdapter) gridView2.getAdapter()).notifyDataSetChanged();
    }


    //checks if the current position is one of the preset values and prevents their change.
    public void colorcell() {

        final MediaPlayer doh = new MediaPlayer().create(this, R.raw.doh);


        String posnumber = String.valueOf(position1);

                    if (Arrays.asList(blanksBackup).contains(posnumber)) {

                            gridView.getChildAt(position1).setBackgroundColor(getResources().getColor(navyblue1));
                    }else{
                        doh.start();
                        Toast.makeText(getApplicationContext(), (CharSequence) ("Can't change preset numbers"), Toast.LENGTH_SHORT).show();
                    }
    }


//just sets all the cellcolors to black as default
    public void initalcolorcell() {
        for (int i = 0; i < numbers.length; i++) {
            gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorPrimaryDark));

        }
        gridView.setBackgroundColor(getResources().getColor(colorPrimaryDark));


    }





//this resets CELL COLOR to black FIRST then highlights the current (CELLS horizontally and vertically)
    public void revertpreviouscolorcell() {

        previousposition1 = currentposiiton;
        currentposiiton = position1;
        initalcolorcell();
        boolean checking;
        if (previousposition1 != position1) {

            gridView.getChildAt(previousposition1).setBackgroundColor(getResources().getColor(colorPrimaryDark));
        }
        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        ((ArrayAdapter) gridView2.getAdapter()).notifyDataSetChanged();
        for (int i = 0; i < numbers.length; i++) {
            for (int j = 0; j <  numbers.length; j++) {
                if ((numbers[i].equals(numbers[position1]) && i != j &&numbers[i]!=" ")) {
                    gridView.getChildAt(i).setBackgroundColor(getResources().getColor(yellowselecttransparent));
                    ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
                    ((ArrayAdapter) gridView2.getAdapter()).notifyDataSetChanged();
                }
            }}
        gridView.getChildAt(position1).setBackgroundColor(getResources().getColor(yellowselect));
        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        ((ArrayAdapter) gridView2.getAdapter()).notifyDataSetChanged();



        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();

//------------------------------------------------------------------------------------------------------------------------------------------
        if (position1 >= 0 && position1 <= 2|| position1 >= 9 && position1 <= 11||position1 >= 18 && position1 <= 20 ) {
            for (int i = 0; i < 3; i++) {
                    gridView.getChildAt(i).setBackgroundColor(getResources().getColor(yellowtransparent));}
            for (int j = 9; j < 12; j++) {
                    gridView.getChildAt(j).setBackgroundColor(getResources().getColor(yellowtransparent));}
            for (int k = 18; k < 21; k++) {
                    gridView.getChildAt(k).setBackgroundColor(getResources().getColor(yellowtransparent));
                    }
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        }

        if (position1 >= 3 && position1 <= 5|| position1 >= 12 && position1 <= 14||position1 >= 21 && position1 <= 23 ) {
            for (int i = 3; i < 6; i++) {
                gridView.getChildAt(i).setBackgroundColor(getResources().getColor(yellowtransparent));}
            for (int j = 12; j < 15; j++) {
                gridView.getChildAt(j).setBackgroundColor(getResources().getColor(yellowtransparent));}
            for (int k = 21; k < 24; k++) {
                gridView.getChildAt(k).setBackgroundColor(getResources().getColor(yellowtransparent));
            }
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        }

        if (position1 >= 6 && position1 <= 8|| position1 >= 15 && position1 <= 17||position1 >= 24 && position1 <= 26 ) {
            for (int i = 6; i < 9; i++) {
                gridView.getChildAt(i).setBackgroundColor(getResources().getColor(yellowtransparent));}
            for (int j = 15; j < 18; j++) {
                gridView.getChildAt(j).setBackgroundColor(getResources().getColor(yellowtransparent));}
            for (int k = 24; k < 27; k++) {
                gridView.getChildAt(k).setBackgroundColor(getResources().getColor(yellowtransparent));
            }
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        }

        if (position1 >= 27 && position1 <= 29|| position1 >= 36 && position1 <= 38||position1 >= 45 && position1 <= 47 ) {
            for (int i = 27; i < 30; i++) {
                gridView.getChildAt(i).setBackgroundColor(getResources().getColor(yellowtransparent));}
            for (int j = 36; j < 39; j++) {
                gridView.getChildAt(j).setBackgroundColor(getResources().getColor(yellowtransparent));}
            for (int k = 45; k < 48; k++) {
                gridView.getChildAt(k).setBackgroundColor(getResources().getColor(yellowtransparent));
            }
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        }

        if (position1 >= 30 && position1 <= 32|| position1 >= 39 && position1 <= 41||position1 >= 48 && position1 <= 50 ) {
            for (int i = 30; i < 33; i++) {
                gridView.getChildAt(i).setBackgroundColor(getResources().getColor(yellowtransparent));}
            for (int j = 39; j < 42; j++) {
                gridView.getChildAt(j).setBackgroundColor(getResources().getColor(yellowtransparent));}
            for (int k = 48; k < 51; k++) {
                gridView.getChildAt(k).setBackgroundColor(getResources().getColor(yellowtransparent));
            }
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        }

        if (position1 >= 33 && position1 <= 35|| position1 >= 42 && position1 <= 44||position1 >= 51 && position1 <= 53 ) {
            for (int i = 33; i < 36; i++) {
                gridView.getChildAt(i).setBackgroundColor(getResources().getColor(yellowtransparent));}
            for (int j = 42; j < 45; j++) {
                gridView.getChildAt(j).setBackgroundColor(getResources().getColor(yellowtransparent));}
            for (int k = 51; k < 54; k++) {
                gridView.getChildAt(k).setBackgroundColor(getResources().getColor(yellowtransparent));
            }
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        }

        ;;

        if (position1 >= 54 && position1 <= 56|| position1 >= 63 && position1 <= 65||position1 >= 72 && position1 <= 74 ) {
            for (int i = 54; i < 57; i++) {
                gridView.getChildAt(i).setBackgroundColor(getResources().getColor(yellowtransparent));}
            for (int j = 63; j < 66; j++) {
                gridView.getChildAt(j).setBackgroundColor(getResources().getColor(yellowtransparent));}
            for (int k = 72; k < 75; k++) {
                gridView.getChildAt(k).setBackgroundColor(getResources().getColor(yellowtransparent));
            }
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        }

        if (position1 >= 57 && position1 <= 59|| position1 >= 66 && position1 <= 68||position1 >= 75 && position1 <= 77 ) {
            for (int i = 57; i < 60; i++) {
                gridView.getChildAt(i).setBackgroundColor(getResources().getColor(yellowtransparent));}
            for (int j = 66; j < 69; j++) {
                gridView.getChildAt(j).setBackgroundColor(getResources().getColor(yellowtransparent));}
            for (int k = 75; k < 78; k++) {
                gridView.getChildAt(k).setBackgroundColor(getResources().getColor(yellowtransparent));
            }
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        }

        if (position1 >= 60 && position1 <= 62|| position1 >= 69 && position1 <= 71||position1 >= 78 && position1 <= 80 ) {
            for (int i = 60; i < 63; i++) {
                gridView.getChildAt(i).setBackgroundColor(getResources().getColor(yellowtransparent));}
            for (int j = 69; j < 72; j++) {
                gridView.getChildAt(j).setBackgroundColor(getResources().getColor(yellowtransparent));}
            for (int k = 78; k < 81; k++) {
                gridView.getChildAt(k).setBackgroundColor(getResources().getColor(yellowtransparent));
            }
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        }





//------------------------------------------------------------------------------------------------------------------------------------------


    //FIRST ROW
            if (position1 >= 0 && position1 <= 8) {
                ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
                int row=0;
                while(row<9){
                gridView.getChildAt(row).setBackgroundColor(getResources().getColor(yellow));
                    row++;
                }}
                ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();

            //SECOND ROW
            if (position1 >= 9 && position1 <= 17) {
                int row=9;
                while(row<18){
                    gridView.getChildAt(row).setBackgroundColor(getResources().getColor(yellow));
                    row++;
                }}
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        //THIRD ROW

            if (position1 >= 18 && position1 <= 26) {
                int row=18;
                while(row<27){
                    gridView.getChildAt(row).setBackgroundColor(getResources().getColor(yellow));
                    row++;
                }}
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();


            if (position1 >= 27 && position1 <= 35) {
                int row=27;
                while(row<36){
                    gridView.getChildAt(row).setBackgroundColor(getResources().getColor(yellow));
                    row++;
                }}
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();


            if (position1 >= 36 && position1 <= 44) {
                int row=36;
                while(row<45){
                    gridView.getChildAt(row).setBackgroundColor(getResources().getColor(yellow));
                    row++;
                }}
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();


            if (position1 >= 45 && position1 <= 53) {
                int row=45;
                while(row<54){
                    gridView.getChildAt(row).setBackgroundColor(getResources().getColor(yellow));
                    row++;
                }}
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();


            if (position1 >= 54 && position1 <= 62) {
                int row=54;
                while(row<63){
                    gridView.getChildAt(row).setBackgroundColor(getResources().getColor(yellow));
                    row++;
                }}
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();



            if (position1 >= 63 && position1 <= 71) {
                int row=63;
                while(row<72){
                    gridView.getChildAt(row).setBackgroundColor(getResources().getColor(yellow));
                    row++;
                }}
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();



        //last row
            if (position1 >= 72 && position1 <= 80) {
                int row=72;
                while(row<81){
                    gridView.getChildAt(row).setBackgroundColor(getResources().getColor(yellow));
                    row++;
                }}
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();

        //columns shading
//VERTICAL HIGHLIGHTS
        int currentY2 = position1;
        int currentY = position1;
        int column3=0;
        int column13=0;
        while(column3<8) {

                while (currentY<81) {
                    gridView.getChildAt(currentY).setBackgroundColor(getResources().getColor(yellow));
                    //THIS MAKES THE NUMBER BELOW THE POSITION THE SAME COLOR UNTIL ITS 81 GOING TOP DOWN
                    currentY=currentY+9;
                }
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
            column3++;
        }

        while (column13<8) {
            while (currentY2>-1) {
                gridView.getChildAt(currentY2).setBackgroundColor(getResources().getColor(yellow));
                //THIS MAKES THE NUMBER BELOW THE POSITION THE SAME COLOR UNTIL ITS 0 GOING DOWN TO TOP
                currentY2=currentY2-9;
            }
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();

            column13++;
        }

        gridView.getChildAt(position1).setBackgroundColor(getResources().getColor(yellowselect));
        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();

        if (previousposition1 == position1) {
            gridView.getChildAt(previousposition1).setBackgroundColor(getResources().getColor(yellowselect));
        }
        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();






    }


//checks and highlights conflicts

    //CHECKS FOR CONFLICTS IN ROWS
    //--------------------------------------------------------------------------------------------------------------------------------------
    public void highlightConflicts(){
        if (position1 >= 0 && position1 <= 8) {
            for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        if ((numbers[i].equals(numbers[j]) && i != j && numbers[i]!=" ")) {
                            gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));
                            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
                        }
                    }
                }
            }

        if (position1 >= 9 && position1 <= 17) {
            for (int i = 9; i < 18; i++) {
                for (int j = 9; j < 18; j++) {
                    if ((numbers[i].equals(numbers[j]) && i != j && numbers[i]!=" ")) {
                        gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));
                        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
                    }
                }
            }
        }

        if (position1 >= 18 && position1 <= 26) {
            for (int i = 18; i < 27; i++) {
                for (int j = 18; j < 27; j++) {
                    if ((numbers[i].equals(numbers[j]) && i != j && numbers[i]!=" ")) {
                        gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));
                        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
                    }
                }
            }
        }

        if (position1 >= 27 && position1 <= 35) {
            for (int i = 27; i < 36; i++) {
                for (int j = 27; j < 36; j++) {
                    if ((numbers[i].equals(numbers[j]) && i != j && numbers[i]!=" ")) {
                        gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));
                        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
                    }
                }
            }
        }

        if (position1 >= 36 && position1 <= 44) {
            for (int i = 36; i < 45; i++) {
                for (int j = 36; j < 45; j++) {
                    if ((numbers[i].equals(numbers[j]) && i != j && numbers[i]!=" ")) {
                        gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));
                        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
                    }
                }
            }
        }

        if (position1 >= 45 && position1 <= 53) {
            for (int i = 45; i < 54; i++) {
                for (int j = 45; j < 54; j++) {
                    if ((numbers[i].equals(numbers[j]) && i != j && numbers[i]!=" ")) {
                        gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));
                        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
                    }
                }
            }
        }

        if (position1 >= 54 && position1 <= 62) {
            for (int i = 54; i < 63; i++) {
                for (int j = 54; j < 63; j++) {
                    if ((numbers[i].equals(numbers[j]) && i != j && numbers[i]!=" ")) {
                        gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));
                        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
                    }
                }
            }
        }

        if (position1 >= 63 && position1 <= 71) {
            for (int i = 63; i < 72; i++) {
                for (int j = 63; j < 72; j++) {
                    if ((numbers[i].equals(numbers[j]) && i != j && numbers[i]!=" ")) {
                        gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));
                        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
                    }
                }
            }
        }

        if (position1 >= 72 && position1 <= 80) {
            for (int i = 72; i < 81; i++) {
                for (int j = 72; j < 81; j++) {
                    if ((numbers[i].equals(numbers[j]) && i != j && numbers[i]!=" ")) {
                        gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));
                        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
                    }
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------------------------------
        //Checks for conflicts in 3x3 square

        if (position1 >= 0 && position1 <= 2|| position1 >= 9 && position1 <= 11||position1 >= 18 && position1 <= 20 ) {
            for (int i = 0; i < 3; i++) {
                if ((numbers[i].equals(numbers[position1]) && i != position1 && numbers[i] != " ")) {
                    gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));}}
            for (int j = 9; j < 12; j++) {
                if ((numbers[j].equals(numbers[position1]) && j != position1 && numbers[j] != " ")) {
                    gridView.getChildAt(j).setBackgroundColor(getResources().getColor(colorAccent));}}
            for (int k = 18; k < 21; k++) {
                if ((numbers[k].equals(numbers[position1]) && k != position1 && numbers[k] != " ")) {
                    gridView.getChildAt(k).setBackgroundColor(getResources().getColor(colorAccent));}}
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();}

        if (position1 >= 3 && position1 <= 5|| position1 >= 12 && position1 <= 14||position1 >= 21 && position1 <= 23 ) {
            for (int i = 3; i < 6; i++) {
                if ((numbers[i].equals(numbers[position1]) && i != position1 && numbers[i] != " ")) {
                    gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));}}
            for (int j = 12; j < 15; j++) {
                if ((numbers[j].equals(numbers[position1]) && j != position1 && numbers[j] != " ")) {
                    gridView.getChildAt(j).setBackgroundColor(getResources().getColor(colorAccent));}}
            for (int k = 21; k < 24; k++) {
                if ((numbers[k].equals(numbers[position1]) && k != position1 && numbers[k] != " ")) {
                    gridView.getChildAt(k).setBackgroundColor(getResources().getColor(colorAccent));}}
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();}

        if (position1 >= 6 && position1 <= 8|| position1 >= 15 && position1 <= 17||position1 >= 24 && position1 <= 26 ) {
            for (int i = 6; i < 9; i++) {
                if ((numbers[i].equals(numbers[position1]) && i != position1 && numbers[i] != " ")) {
                    gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));}}
            for (int j = 15; j < 18; j++) {
                if ((numbers[j].equals(numbers[position1]) && j != position1 && numbers[j] != " ")) {
                    gridView.getChildAt(j).setBackgroundColor(getResources().getColor(colorAccent));}}
            for (int k = 24; k < 27; k++) {
                if ((numbers[k].equals(numbers[position1]) && k != position1 && numbers[k] != " ")) {
                    gridView.getChildAt(k).setBackgroundColor(getResources().getColor(colorAccent));}}
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();}

        if (position1 >= 27 && position1 <= 29|| position1 >= 36 && position1 <= 38||position1 >= 45 && position1 <= 47 ) {
            for (int i = 27; i < 30; i++) {
                if ((numbers[i].equals(numbers[position1]) && i != position1 && numbers[i] != " ")) {
                    gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));}}
            for (int j = 36; j < 39; j++) {
                if ((numbers[j].equals(numbers[position1]) && j != position1 && numbers[j] != " ")) {
                    gridView.getChildAt(j).setBackgroundColor(getResources().getColor(colorAccent));}}
            for (int k = 45; k < 48; k++) {
                if ((numbers[k].equals(numbers[position1]) && k != position1 && numbers[k] != " ")) {
                    gridView.getChildAt(k).setBackgroundColor(getResources().getColor(colorAccent));}}
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();}

        if (position1 >= 30 && position1 <= 32|| position1 >= 39 && position1 <= 41||position1 >= 48 && position1 <= 50 ) {
            for (int i = 30; i < 33; i++) {
                if ((numbers[i].equals(numbers[position1]) && i != position1 && numbers[i] != " ")) {
                    gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));}}
            for (int j = 39; j < 42; j++) {
                if ((numbers[j].equals(numbers[position1]) && j != position1 && numbers[j] != " ")) {
                    gridView.getChildAt(j).setBackgroundColor(getResources().getColor(colorAccent));}}
            for (int k = 48; k < 51; k++) {
                if ((numbers[k].equals(numbers[position1]) && k != position1 && numbers[k] != " ")) {
                    gridView.getChildAt(k).setBackgroundColor(getResources().getColor(colorAccent));}}
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();}

        if (position1 >= 33 && position1 <= 35|| position1 >= 42 && position1 <= 44||position1 >= 51 && position1 <= 53 ) {
            for (int i = 33; i < 36; i++) {
                if ((numbers[i].equals(numbers[position1]) && i != position1 && numbers[i] != " ")) {
                    gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));}}
            for (int j = 42; j < 45; j++) {
                if ((numbers[j].equals(numbers[position1]) && j != position1 && numbers[j] != " ")) {
                    gridView.getChildAt(j).setBackgroundColor(getResources().getColor(colorAccent));}}
            for (int k = 51; k < 54; k++) {
                if ((numbers[k].equals(numbers[position1]) && k != position1 && numbers[k] != " ")) {
                    gridView.getChildAt(k).setBackgroundColor(getResources().getColor(colorAccent));}}
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();}

        if (position1 >= 54 && position1 <= 56|| position1 >= 63 && position1 <= 65||position1 >= 72 && position1 <= 74 ) {
            for (int i = 54; i < 57; i++) {
                if ((numbers[i].equals(numbers[position1]) && i != position1 && numbers[i] != " ")) {
                    gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));}}
            for (int j = 63; j < 66; j++) {
                if ((numbers[j].equals(numbers[position1]) && j != position1 && numbers[j] != " ")) {
                    gridView.getChildAt(j).setBackgroundColor(getResources().getColor(colorAccent));}}
            for (int k = 72; k < 75; k++) {
                if ((numbers[k].equals(numbers[position1]) && k != position1 && numbers[k] != " ")) {
                    gridView.getChildAt(k).setBackgroundColor(getResources().getColor(colorAccent));}}
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();}

        if (position1 >= 57 && position1 <= 59|| position1 >= 66 && position1 <= 68||position1 >= 75 && position1 <= 77 ) {
            for (int i = 57; i < 60; i++) {
                if ((numbers[i].equals(numbers[position1]) && i != position1 && numbers[i] != " ")) {
                    gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));}}
            for (int j = 66; j < 69; j++) {
                if ((numbers[j].equals(numbers[position1]) && j != position1 && numbers[j] != " ")) {
                    gridView.getChildAt(j).setBackgroundColor(getResources().getColor(colorAccent));}}
            for (int k = 75; k < 78; k++) {
                if ((numbers[k].equals(numbers[position1]) && k != position1 && numbers[k] != " ")) {
                    gridView.getChildAt(k).setBackgroundColor(getResources().getColor(colorAccent));}}
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();}

        if (position1 >= 60 && position1 <= 62|| position1 >= 69 && position1 <= 71||position1 >= 78 && position1 <= 80 ) {
            for (int i = 60; i < 63; i++) {
                if ((numbers[i].equals(numbers[position1]) && i != position1 && numbers[i] != " ")) {
                    gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));}}
            for (int j = 69; j < 72; j++) {
                if ((numbers[j].equals(numbers[position1]) && j != position1 && numbers[j] != " ")) {
                    gridView.getChildAt(j).setBackgroundColor(getResources().getColor(colorAccent));}}
            for (int k = 78; k < 81; k++) {
                if ((numbers[k].equals(numbers[position1]) && k != position1 && numbers[k] != " ")) {
                    gridView.getChildAt(k).setBackgroundColor(getResources().getColor(colorAccent));}}
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();}




        //---------------------------------------------------------------------------------------------------------------------------------------------


        //Checks for conflicts in columns
        int currentY2 = position1;
        int currentY = position1;


            while (currentY<81) {
                    for (int i = position1; i < 81; i=i+9) {
                        for (int j = position1; j < 81; j=j+9) {
                            if ((numbers[i].equals(numbers[j]) && i != j && numbers[i]!=" ")) {
                gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));}
                //THIS MAKES THE NUMBER BELOW THE POSITION THE SAME COLOR UNTIL ITS 81 GOING TOP DOWN

            }}
                currentY=currentY+9;
            }
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();



            while (currentY2>-1) {
                for (int i = position1; i > -1; i=i-9) {
                    for (int j = position1; j >-1; j=j-9) {
                        if ((numbers[i].equals(numbers[j]) && i != j && numbers[i]!=" ")) {
                            gridView.getChildAt(i).setBackgroundColor(getResources().getColor(colorAccent));}}}
                //THIS MAKES THE NUMBER BELOW THE POSITION THE SAME COLOR UNTIL ITS 0 GOING DOWN TO TOP
                currentY2=currentY2-9;
            }
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();

        }


//sets the current position two the value of the number clicked on from the second gridview at the bottom and sets the text color to blue
    public void popup() {
        int previousposition1 = -1;
        int temposition1 = -1;
        int firstclick;
        int secondclick;
        try {

                        previousposition1 = -1;
            for (int i = 0; i < numbers.length; i++) {

                for (int j = 0; j < blanksBackup.length; j++) {
                    if (String.valueOf(position1) ==blanksBackup[j]) {

                        temposition1 = position1;

                        firstclick = -1;
                        secondclick = -1;
                        firstclick = position1;
                        secondclick = positionnumber;

                        numbers[firstclick] = String.valueOf(numbervalues[secondclick]);
                        TextView text = (TextView) gridView.getChildAt(position1).findViewById(text1);
                        text.setTextColor(getResources().getColor(navyblue));

                        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
                        gridView.getChildAt(position1).setBackgroundColor(getResources().getColor(yellowselect));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        ((ArrayAdapter) gridView2.getAdapter()).notifyDataSetChanged();
        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
    }


    public void highlightrestofselected() {

    }
    public void savestate(){
        if (Arrays.equals(defaultnumbers, savenumbers)&& Arrays.equals(numbers, savenumbers)) {
            Toast.makeText(getApplicationContext(), (CharSequence) ("Cannot save default state"), Toast.LENGTH_SHORT).show();
        }else {
        Toast.makeText(getApplicationContext(), (CharSequence) ("Saved Checkpoint"), Toast.LENGTH_SHORT).show();
    List<Integer> savedlist = new ArrayList<Integer>();
    for (int i = 0; i < numbers.length; i++) {
        savenumbers = numbers.clone();
        }
        }
    }



    protected void loadstate(){

    int savedposition=position1;
        if (Arrays.equals(defaultnumbers, savenumbers)){
            Toast.makeText(getApplicationContext(), (CharSequence) ("No Checkpoint available"), Toast.LENGTH_SHORT).show();
                }else if (Arrays.equals(numbers, savenumbers)) {
                 Toast.makeText(getApplicationContext(), (CharSequence) ("Current Checkpoint already loaded"), Toast.LENGTH_SHORT).show();
        }else if (!Arrays.equals(defaultnumbers, savenumbers)&& !Arrays.equals(numbers, savenumbers)) {
            Toast.makeText(getApplicationContext(), (CharSequence) ("Checkpoint loaded"), Toast.LENGTH_SHORT).show();
            List<Integer> loadlist = new ArrayList<Integer>();
            for (int i = 0; i < numbers.length; i++) {
                numbers[i] = savenumbers[i];

            }
        }
      //  System.out.println("TESTING ARRAYnumbers" + Arrays.toString(numbers));

        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        initalcolorcell();
        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        position1=savedposition;
        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
    }



    public void testwin() {


        testing();
    }
//tests if the current numbers grid view equals the default saved originally generated SOLVED grid
    public void testing() {

        final MediaPlayer mp = new MediaPlayer().create(this, R.raw.win);

        final int delay_only_win_message=600;
        //IMMEDIATELY RECOLOR THE GRID TO SHOW ITS SOLVED/WON
        if (Arrays.equals(numbers, copynumbers)) {
            for (int i = 0; i < numbers.length; i++) {
                    TextView wintext = (TextView) gridView.getChildAt(i).findViewById(text1);
                   wintext.setTextColor(getResources().getColor(win));
                gridView.getChildAt(i).setBackgroundColor(getResources().getColor(transparent));
            }
            gridView.setBackgroundColor(getResources().getColor(transparent));
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Arrays.equals(numbers, copynumbers)) {

                    Do_the_alert++;
                    //ALERT THE USER WHEN THE GAME IS SOLVED WITH DELAY
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    mp.start();
                    alertDialog.setTitle("CONGRATULATIONS");
                    alertDialog.setMessage(" YOU WIN!!! ");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Restart?",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    ChooseDifficulty();
                                }
                            });

                    if (Do_the_alert == 1) {
                        Do_the_alert--;
                        alertDialog.show();
                    }
                }
            }
            //DELAY TIME OF ONLY THE MESSAGE
        }, delay_only_win_message);}

    public void ChooseDifficulty() {



        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Sudoku Galaxy");
        alertDialog.setMessage(" Choose Difficulty...at your own peril ");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "TEST",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        difVal = 4;
                        reset();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "HARD",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        difVal = 1;
                        reset();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "MEDIUM",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        difVal = 2;
                        reset();
                    }
                });

        alertDialog.show();
    }



    void reset() {
        Intent intent = getIntent().putExtra("Difficulty", difVal);
        finish();
        for(int i=0; i < mp.length - 1; i++)
            mp[i].stop();
        startActivity(intent);
    }
}

/*
*  CREATED BY ABDELRAHMAN OBYAT AND SCOTT FENTON
*  CREATED FOR UMASS BOSTON COMPUTER SCIENCE 443
 */

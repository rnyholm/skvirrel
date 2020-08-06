package ax.stardust.skvirrel.component.keyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;

import ax.stardust.skvirrel.R;

public class AlphanumericKeyboard extends SkvirrelKeyboard {
    private Button buttonA;
    private Button buttonB;
    private Button buttonC;
    private Button buttonD;
    private Button buttonE;
    private Button buttonF;
    private Button buttonG;
    private Button buttonH;
    private Button buttonI;
    private Button buttonJ;
    private Button buttonK;
    private Button buttonL;
    private Button buttonM;
    private Button buttonN;
    private Button buttonO;
    private Button buttonP;
    private Button buttonQ;
    private Button buttonR;
    private Button buttonS;
    private Button buttonT;
    private Button buttonU;
    private Button buttonV;
    private Button buttonW;
    private Button buttonX;
    private Button buttonY;
    private Button buttonZ;
    private Button buttonDot;
    private Button buttonDash;

    public AlphanumericKeyboard(Context context) {
        super(context);
    }

    public AlphanumericKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlphanumericKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void inflateLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.alpha_numeric_keyboard, this, true);
    }

    @Override
    protected void findViews() {
        button0 = findViewById(R.id.alphanumeric_keyboard_button_0);
        button1 = findViewById(R.id.alphanumeric_keyboard_button_1);
        button2 = findViewById(R.id.alphanumeric_keyboard_button_2);
        button3 = findViewById(R.id.alphanumeric_keyboard_button_3);
        button4 = findViewById(R.id.alphanumeric_keyboard_button_4);
        button5 = findViewById(R.id.alphanumeric_keyboard_button_5);
        button6 = findViewById(R.id.alphanumeric_keyboard_button_6);
        button7 = findViewById(R.id.alphanumeric_keyboard_button_7);
        button8 = findViewById(R.id.alphanumeric_keyboard_button_8);
        button9 = findViewById(R.id.alphanumeric_keyboard_button_9);
        buttonA = findViewById(R.id.alphanumeric_keyboard_button_a);
        buttonB = findViewById(R.id.alphanumeric_keyboard_button_b);
        buttonC = findViewById(R.id.alphanumeric_keyboard_button_c);
        buttonD = findViewById(R.id.alphanumeric_keyboard_button_d);
        buttonE = findViewById(R.id.alphanumeric_keyboard_button_e);
        buttonF = findViewById(R.id.alphanumeric_keyboard_button_f);
        buttonG = findViewById(R.id.alphanumeric_keyboard_button_g);
        buttonH = findViewById(R.id.alphanumeric_keyboard_button_h);
        buttonI = findViewById(R.id.alphanumeric_keyboard_button_i);
        buttonJ = findViewById(R.id.alphanumeric_keyboard_button_j);
        buttonK = findViewById(R.id.alphanumeric_keyboard_button_k);
        buttonL = findViewById(R.id.alphanumeric_keyboard_button_l);
        buttonM = findViewById(R.id.alphanumeric_keyboard_button_m);
        buttonN = findViewById(R.id.alphanumeric_keyboard_button_n);
        buttonO = findViewById(R.id.alphanumeric_keyboard_button_o);
        buttonP = findViewById(R.id.alphanumeric_keyboard_button_p);
        buttonQ = findViewById(R.id.alphanumeric_keyboard_button_q);
        buttonR = findViewById(R.id.alphanumeric_keyboard_button_r);
        buttonS = findViewById(R.id.alphanumeric_keyboard_button_s);
        buttonT = findViewById(R.id.alphanumeric_keyboard_button_t);
        buttonU = findViewById(R.id.alphanumeric_keyboard_button_u);
        buttonV = findViewById(R.id.alphanumeric_keyboard_button_v);
        buttonW = findViewById(R.id.alphanumeric_keyboard_button_w);
        buttonX = findViewById(R.id.alphanumeric_keyboard_button_x);
        buttonY = findViewById(R.id.alphanumeric_keyboard_button_y);
        buttonZ = findViewById(R.id.alphanumeric_keyboard_button_z);
        buttonDot = findViewById(R.id.alphanumeric_keyboard_button_dot);
        buttonDash = findViewById(R.id.alphanumeric_keyboard_button_dash);
        buttonDelete = findViewById(R.id.alphanumeric_keyboard_button_del);
    }

    @Override
    protected void setListeners() {
        button0.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        buttonA.setOnClickListener(this);
        buttonB.setOnClickListener(this);
        buttonC.setOnClickListener(this);
        buttonD.setOnClickListener(this);
        buttonE.setOnClickListener(this);
        buttonF.setOnClickListener(this);
        buttonG.setOnClickListener(this);
        buttonH.setOnClickListener(this);
        buttonI.setOnClickListener(this);
        buttonJ.setOnClickListener(this);
        buttonK.setOnClickListener(this);
        buttonL.setOnClickListener(this);
        buttonM.setOnClickListener(this);
        buttonN.setOnClickListener(this);
        buttonO.setOnClickListener(this);
        buttonP.setOnClickListener(this);
        buttonQ.setOnClickListener(this);
        buttonR.setOnClickListener(this);
        buttonS.setOnClickListener(this);
        buttonT.setOnClickListener(this);
        buttonU.setOnClickListener(this);
        buttonV.setOnClickListener(this);
        buttonW.setOnClickListener(this);
        buttonX.setOnClickListener(this);
        buttonY.setOnClickListener(this);
        buttonZ.setOnClickListener(this);
        buttonDot.setOnClickListener(this);
        buttonDash.setOnClickListener(this);
        // delete button needs this many listeners in order to handle long press delete
        buttonDelete.setOnClickListener(this);
        buttonDelete.setOnLongClickListener(this);
    }

    @Override
    protected void setKeyValues() {
        keyValues.put(R.id.alphanumeric_keyboard_button_0, "0");
        keyValues.put(R.id.alphanumeric_keyboard_button_1, "1");
        keyValues.put(R.id.alphanumeric_keyboard_button_2, "2");
        keyValues.put(R.id.alphanumeric_keyboard_button_3, "3");
        keyValues.put(R.id.alphanumeric_keyboard_button_4, "4");
        keyValues.put(R.id.alphanumeric_keyboard_button_5, "5");
        keyValues.put(R.id.alphanumeric_keyboard_button_6, "6");
        keyValues.put(R.id.alphanumeric_keyboard_button_7, "7");
        keyValues.put(R.id.alphanumeric_keyboard_button_8, "8");
        keyValues.put(R.id.alphanumeric_keyboard_button_9, "9");
        keyValues.put(R.id.alphanumeric_keyboard_button_a, "A");
        keyValues.put(R.id.alphanumeric_keyboard_button_b, "B");
        keyValues.put(R.id.alphanumeric_keyboard_button_c, "C");
        keyValues.put(R.id.alphanumeric_keyboard_button_d, "D");
        keyValues.put(R.id.alphanumeric_keyboard_button_e, "E");
        keyValues.put(R.id.alphanumeric_keyboard_button_f, "F");
        keyValues.put(R.id.alphanumeric_keyboard_button_g, "G");
        keyValues.put(R.id.alphanumeric_keyboard_button_h, "H");
        keyValues.put(R.id.alphanumeric_keyboard_button_i, "I");
        keyValues.put(R.id.alphanumeric_keyboard_button_j, "J");
        keyValues.put(R.id.alphanumeric_keyboard_button_k, "K");
        keyValues.put(R.id.alphanumeric_keyboard_button_l, "L");
        keyValues.put(R.id.alphanumeric_keyboard_button_m, "M");
        keyValues.put(R.id.alphanumeric_keyboard_button_n, "N");
        keyValues.put(R.id.alphanumeric_keyboard_button_o, "O");
        keyValues.put(R.id.alphanumeric_keyboard_button_p, "P");
        keyValues.put(R.id.alphanumeric_keyboard_button_q, "Q");
        keyValues.put(R.id.alphanumeric_keyboard_button_r, "R");
        keyValues.put(R.id.alphanumeric_keyboard_button_s, "S");
        keyValues.put(R.id.alphanumeric_keyboard_button_t, "T");
        keyValues.put(R.id.alphanumeric_keyboard_button_u, "U");
        keyValues.put(R.id.alphanumeric_keyboard_button_v, "V");
        keyValues.put(R.id.alphanumeric_keyboard_button_w, "W");
        keyValues.put(R.id.alphanumeric_keyboard_button_x, "X");
        keyValues.put(R.id.alphanumeric_keyboard_button_y, "Y");
        keyValues.put(R.id.alphanumeric_keyboard_button_z, "Z");
        keyValues.put(R.id.alphanumeric_keyboard_button_dot, ".");
        keyValues.put(R.id.alphanumeric_keyboard_button_dash, "-");
    }
}

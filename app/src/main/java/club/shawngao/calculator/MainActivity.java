package club.shawngao.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // 定义按钮的数组
    private Button[] buttons = new Button[23];
    // 定义按钮id数组
    private int[] ids = new int[]{R.id.opleft, R.id.opright, R.id.opmult, R.id.opdiv, R.id.opand,
            R.id.opsub, R.id.opmod, R.id.opequal, R.id.num0, R.id.num00, R.id.num1, R.id.num2, R.id.num3,
            R.id.num4, R.id.num5, R.id.num6, R.id.num7, R.id.num8, R.id.num9, R.id.numpoint, R.id.c, R.id.back, R.id.exit};
    private TextView textView;  // 显示算式
    private TextView resultTextView;    // 显示结果
    private TextView testTextView;  // 测试用
    private TextView eTextView; // 显示异常
    private static String expression = "0.00";  // 算式字符串，默认0.00
    private boolean flag = false;   // 是否开始计算的标志
    private String res = "0.00";    // res，全：result，结果字符串
    private static String ex = "";  // 异常字符串

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int i = 0; i < ids.length; i++) {
            buttons[i] = findViewById(ids[i]);
            buttons[i].setOnClickListener(this);
        }
        textView = (TextView)findViewById(R.id.expression);
        resultTextView = (TextView)findViewById(R.id.result);
        testTextView = (TextView)findViewById(R.id.test);
        eTextView = (TextView)findViewById(R.id.displayException);
        textView.setText("算式:" + expression);
        resultTextView.setText("计算结果:" + res);
        testTextView.setText("输入的算式(测试用):" + expression);
        expression = "";
        res = "";
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        Button button = (Button)view.findViewById(id);
        String str = button.getText().toString();
        if ((!isNumericzidai(str)) && !(str.equals("."))) {
            if (str.equals("×")) {
                str = "*";
            }
            if (str.equals("÷")) {
                str = "/";
            }
        }
        switch (str) {
            case "C":
                textView.setText("算式:" + "0.00");
                resultTextView.setText("计算结果:" + "0.00");
                testTextView.setText("输入的算式(测试用):" + "NULL");
                eTextView.setText("异常:" + "已清空");
                expression = "";
                res = "";
                ex = "";
                return;
            case "←":
                if (expression.length() >= 1) {
                    expression = expression.substring(0, expression.length() - 1);
                }
                str = "";
                break;
            case "⏏":
                finish();
                System.exit(0);
        }
        strCat(str);
        if (flag) {
            res = calcu();
            resultTextView.setText("计算结果:" + res);
            eTextView.setText("异常:\n" + ex);
            flag = false;
            expression = "";
            ex = "";
        } else {
            textView.setText("算式:" + expression);
            resultTextView.setText("计算结果:" + res);
        }
    }

    // 字符串连接
    private void strCat(String sTmp) {
        if (sTmp.equals("=")) {
            flag = true;
        } else {
            if (expression.isEmpty()) {
                expression += sTmp;
            } else if (isNumericzidai(expression.substring(expression.length() - 1)) && !isNumericzidai(sTmp)) {
                expression += sTmp;
            } else if (!isNumericzidai(expression.substring(expression.length() - 1)) && isNumericzidai(sTmp)) {
                expression += sTmp;
            } else if (isNumericzidai(expression.substring(expression.length() - 1)) && isNumericzidai(sTmp)) {
                expression += sTmp;
            }
        }
    }

    // 计算
    @SuppressLint("SetTextI18n")
    private String calcu() {
        expression += "=";
        testTextView.setText("输入的算式(测试用):" + expression);
        /*
         *  动态数组的定义
         *  al数组用来输入原始算式，即中缀表达式
         *  rpn数组用来存放逆波兰式
         *  op数组用来存放操作符
         */
        ArrayList<String> al = new ArrayList<>();
        ArrayList<String> rpn = new ArrayList<>();
        ArrayList<String> op = new ArrayList<>();
        ArrayList<String> num = new ArrayList<>();
        // numTmp 存放临时的数字字符, opTmp即存放临时的操作符
        StringBuilder numTmp = new StringBuilder();
        String opTmp = "";
        // 用来取出字符串中的单个字符
        int i, j;
        char s;
        if (expression.length() <= 1) {
            return "0.00";
        }
        for (int w = 0; w < expression.length(); w++) {
            s = expression.charAt(w);
            // 如果s存放的是数字，则需要在opTmp处于非空的情况下清空，且需要将操作符之间的数字链接起来（主要用于多位数）
            if ((s >= '0' && s <= '9') || s == '.') {
                if (!opTmp.equals("")) {
                    opTmp = "";
                }
                numTmp.append(s);
            }
            // 如果s中存放的不是数字，则需要在numTmp处于非空的状态下追加到al字符串动态数组的后方，并清空
            // 判断s是否存放的是等号，如果是，则跳出，即标志为算式的结束
            // 判断s存放的是否为"回括号"，如果是，则将s存入opTmp中，再将opTmp追加到al字符串动态数组的后方
            // 其余的情况，则将s存入opTmp中，再将opTmp追加到al字符串动态数组的后方
            if (!(s >= '0' && s <= '9') && s != '.') {
                if (!numTmp.toString().equals("")) {
                    al.add(numTmp.toString());
                    numTmp = new StringBuilder();
                }
                if (s == '=') {
                    break;
                } else if (s == ')' || s == '）'){
                    opTmp = s + "";
                    al.add(opTmp);
                } else {
                    opTmp = s + "";
                    al.add(opTmp);
                }
            }
        }
        if (!isNumericzidai(al.get(0))) {
            al.add(0, "0");
        }
        // 中缀表达式转换逆波兰式
        // 从头至尾扫描中缀表达式
        first: for (i = 0; i <= al.size() - 1; i++) {
            // 如果是操作数，直接放入num中
            if (isNumericzidai(al.get(i))) {
                num.add(al.get(i));
            } else {
                //如果是运算符
                for (;;) {
                    // 如果op为空，或者op末尾元素是左括号，则直接将运算符放入op中
                    // 否则，再看当前运算符的优先级是否与op中最高的元素的优先级高

                    // 如果高，则直接将运算符放入op中
                    // 如果以上2个条件都不满足，则将op的运算符移动至num的末尾
                    // 并再次与op的末尾元素进行比较

                    // 如果遇到括号，则判断是左括号还是右括号
                    // 如果是左括号，则将左括号直接放入op中
                    // 如果是右括号，则将op中左括号后面的运算符反序移动至num中，并删除左括号
                    if (op.isEmpty() || op.get(op.size() - 1).equals("(")) {
                        op.add(al.get(i));
                        break;
                    } else if (JudgmentPriority(al.get(i)) > JudgmentPriority(op.get(op.size() - 1))) {
                        op.add(al.get(i));
                        break;
                    } else if (al.get(i).equals("=")) {
                        break first;
                    } else if (al.get(i).equals("(")) {
                        op.add(al.get(i));
                        break;
                    } else if (al.get(i).equals(")")){
                        for (j = op.size() - 1; j >= 0; j--) {
                            if (op.get(j).equals("(")) {
                                op.remove(j);
                                break;
                            }
                            num.add(op.get(j));
                            op.remove(j);
                        }
                        break;
                    } else {
                        num.add(op.get(op.size() - 1));
                        op.remove(op.size() - 1);
                    }
                }
            }
        }
        // 将num和op都追加到RPN内
        rpn.addAll(num);
        rpn.addAll(op);
        // 对逆波兰式进行解析运算
        String operator = "+-%*/";
        Stack<Double> stack = new Stack<>(); // 栈
        Double a, b;
        for (i = 0; i < rpn.size(); i++) {
            if (!operator.contains(rpn.get(i))) {
                stack.push(Double.parseDouble(rpn.get(i)));
            } else {
                a = stack.pop();
                b = stack.pop();
                switch (operator.indexOf(rpn.get(i))) {
                    case 0:
                        stack.push(a + b);
                        break;
                    case 1:
                        stack.push(b - a);
                        break;
                    case 2:
                        stack.push(b % a);
                        break;
                    case 3:
                        stack.push(a * b);
                        break;
                    case 4:
                        stack.push(b / a);
                        break;
                }
            }
        }
        return String.valueOf(stack.pop());
    }

    /**
     * 判定优先级
     * @param op    操作符
     * @return  返回优先级
     */
    public static int JudgmentPriority(String op) {
        switch (op) {
            case "+":
            case "-":   return 1;
            case "%":
            case "*":
            case "/":   return 2;
            default:    return 0;
        }
    }

    /**
     * 判断是否为数字，不是则会抛出异常
     * @param str   参数
     * @return  返回逻辑值
     */
    public static boolean isNumericzidai(String str) {
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            ex += String.valueOf(e) + "\n";
            return false;
        }
        return true;
    }
}
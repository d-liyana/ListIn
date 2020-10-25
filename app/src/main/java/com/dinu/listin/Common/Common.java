package com.dinu.listin.Common;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.TextView;

import com.dinu.listin.Model.Category;
import com.dinu.listin.Model.Item;
import com.dinu.listin.Model.Size;
import com.dinu.listin.Model.User;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;

public class Common {
    public static final String USER_REF = "User";
    public static final String POPULAR_CATEGORY_REF = "MostPopular";
    public static final String BEST_DEAL_REF = "MostPopular";
    public static final int DEFAUL_COLUMN_COUNT = 0;
    public static final int FULL_WIDTH_COLUMN =1 ;
    public static final String CAT_REF = "Category";
    public static final String ORDER_REF = "Orders";

    public static User currentUser;
    public static String email;


    public static Category categorySelected;
    public static Item selectedItem;
    public static String userid;

    public static String formatPrice(double price) {
        if (price != 0){
            DecimalFormat df=new DecimalFormat("#,##0.00");
            df.setRoundingMode(RoundingMode.UP);
            String finalPrice=new StringBuilder(df.format(price)).toString();
            return finalPrice.replace(".",",");
        }else {
            return "0.00";
        }
    }

    public static Double calculateExtraPrice(Size userSelectedSize) {
        Double result = 0.0;
        if (userSelectedSize == null){
            return 0.0;
        }

        else {
            result= userSelectedSize.getPrice() * 1.0;
            return result;
        }
    }

    public static void setSpanString(String welcome, String name, TextView textView) {
        SpannableStringBuilder builder=new SpannableStringBuilder();
        builder.append(welcome);
        SpannableString spannableString=new SpannableString(name);
        StyleSpan boldSpan=new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan,0,name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(spannableString);
        textView.setText(builder,TextView.BufferType.SPANNABLE );

    }

    public static String createOrderNumber() {
         return new StringBuilder().append(System.currentTimeMillis())
                 .append(Math.abs(new Random().nextInt()))
                 .toString();
    }

    public static String getDateOfweek(int i) {
        switch (i){
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            case 7:
                return "Sunday";
            default:
                return "Unknown";

        }


    }
}

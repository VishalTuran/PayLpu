package com.csemaster.paylpu;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.csemaster.paylpu.Modals.CartModel;

public class CartDatabase extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME="CartDetails";
    public static final String TABLE_NAME="ProductDetail";
    public static final String KEY_NAME="Name";
    public static String KEY_QUANTITY="Quantity";
    public static final String KEY_PRICE="Image";

    public CartDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"("+KEY_NAME+" TEXT PRIMARY KEY,"+KEY_QUANTITY+" INTEGER,"+KEY_PRICE+" TEXT"+")";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);


        //creating the table again
        onCreate(sqLiteDatabase);

    }

    public void addToCart(CartModel cartModel)
    {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(KEY_NAME,cartModel.getName());
        contentValues.put(KEY_PRICE,cartModel.getPrice());
        //contentValues.put(KEY_QUANTITY,cartModel.getQuantity());
    }


}

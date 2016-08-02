package database.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.BoringLayout;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import product.manager.Product;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Products";
    private static final String TABLE_PRODUCT = "product";
    // Products Table Columns names
    private static final String KEY_ID = "productId";
    private static final String KEY_CODE = "productCode";
    private static final String KEY_DESC = "productDescription";
    private static final String KEY_DATE = "dateCreated";
    private static final String KEY_EX_DATE = "expiryDate";
    ;

    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE "
            + TABLE_PRODUCT + "(" + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_CODE + " TEXT, " + KEY_DESC + " TEXT, " + KEY_DATE + " TEXT, " + KEY_EX_DATE + " TEXT )";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        onCreate(db);
    }

    /*
    Method that fetches product details
    @Param product_code
    Return product as a list
    */
    public List<Product> getListProducts(String product_code) {
        List<Product> prod = new ArrayList<Product>();
        // Select All Query
        String selectQuery = "  select * from " + TABLE_PRODUCT + " where productCode = " + product_code + " ";
        Log.d("NoFakes", "Query Logged: " + selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setProductId(Integer.parseInt(cursor.getString(0)));
                product.setProductCode(cursor.getString(1));
                product.setProductDescription(cursor.getString(2));
                product.setDateCreated(cursor.getString(3));
                product.setExpiryDate(cursor.getString(4));
                // Adding contact to list
                prod.add(product);
            } while (cursor.moveToNext());
        }
        Log.d("NoFakes", "Product List to return-: " + prod.toString());
        // return contact list
        return prod;
    }

    /*
     Method to addProducts
     @Param Products object
     */
    public void addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, product.getProductId()); // product id
        values.put(KEY_CODE, product.getProductCode()); // product code
        values.put(KEY_DESC, product.getProductDescription());//product description
        values.put(KEY_DATE, product.getDateCreated()); // date created
        values.put(KEY_EX_DATE, product.getExpiryDate()); // expiry date
        // Inserting Row
        db.insert(TABLE_PRODUCT, null, values);
        // Closing database connection
        db.close();
    }

    /*
    Method addAllProducts
    adds all products to the database
    for validation
     */
    public void addAllProducts() {
        Log.d("NoFakes", "Adding products to database");
        addProduct(new Product(1, "201309", "Genuine listed product", "2017-09-09", "2018-09-09"));
        addProduct(new Product(2, "201310","Genuine listed product", "2017-09-09", "2018-09-09"));
        addProduct(new Product(3, "201311","Genuine listed product","2017-09-09", "2018-09-09"));
        addProduct(new Product(4, "201312","Genuine listed product","2017-09-09", "2018-09-09"));
        addProduct(new Product(5, "123456","Genuine listed product","2017-09-09", "2018-09-09"));
    }
}

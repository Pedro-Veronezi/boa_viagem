package br.com.casadocodigo.boaviagem.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import br.com.casadocodigo.boaviagem.DatabaseHelper;

/**
 * Created by veronezi on 15/04/14.
 */
public class BoaViagemProvider extends ContentProvider{

    private static final int VIAGENS = 1;
    private static final int VIAGEM_ID = 2;
    private static final int GASTOS = 3;
    private static final int GASTO_ID = 4;
    private static final int GASTOS_VIAGEM_ID = 5;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private DatabaseHelper helper;

    static{
        uriMatcher.addURI(BoaViagemContract.AUTHORITY, BoaViagemContract.VIAGEM_PATH, VIAGENS);
        uriMatcher.addURI(BoaViagemContract.AUTHORITY, BoaViagemContract.VIAGEM_PATH + "/#", VIAGEM_ID);
        uriMatcher.addURI(BoaViagemContract.AUTHORITY, BoaViagemContract.GASTO_PATH, GASTOS);
        uriMatcher.addURI(BoaViagemContract.AUTHORITY, BoaViagemContract.GASTO_PATH + "/#", GASTO_ID);
        uriMatcher.addURI(BoaViagemContract.AUTHORITY,
                BoaViagemContract.GASTO_PATH + "/"+ BoaViagemContract.VIAGEM_PATH + "/#", GASTOS_VIAGEM_ID);
    }

    @Override
    public boolean onCreate() {
        helper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder){
        SQLiteDatabase database = helper.getReadableDatabase();

        switch (uriMatcher.match(uri)){
            case VIAGENS:
                return database.query(BoaViagemContract.VIAGEM_PATH, projection, selection,
                        selectionArgs, null, null, sortOrder);
            case VIAGEM_ID:
                selection = BoaViagemContract.Viagem._ID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                return database.query(BoaViagemContract.VIAGEM_PATH, projection,selection,
                        selectionArgs,null,null,sortOrder);
            case GASTOS:
                database.query(BoaViagemContract.GASTO_PATH, projection, selection,
                        selectionArgs, null, null, sortOrder);
            case GASTOS_VIAGEM_ID:
                selection = BoaViagemContract.Viagem._ID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                return database.query(BoaViagemContract.VIAGEM_PATH, projection,selection,
                        selectionArgs,null,null,sortOrder);
            default:
                throw  new IllegalArgumentException("Uri desconhecida");
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = helper.getWritableDatabase();
        long id;

        switch (uriMatcher.match(uri)) {
            case VIAGENS:
                id = database.insert(BoaViagemContract.VIAGEM_PATH, null,contentValues);
                return Uri.withAppendedPath(BoaViagemContract.Viagem.CONTENT_URI, String.valueOf(id));
            case GASTOS:
                id = database.insert(BoaViagemContract.GASTO_PATH, null, contentValues);
                return Uri.withAppendedPath(BoaViagemContract.Gasto.CONTENT_URI, String.valueOf(id));
            default:
                throw  new IllegalArgumentException("Uri desconhecida");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = helper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case VIAGEM_ID:
                selection = BoaViagemContract.Viagem._ID + " = ?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                return database.delete(BoaViagemContract.VIAGEM_PATH,
                        selection, selectionArgs);
            case GASTO_ID:
                selection = BoaViagemContract.Gasto._ID + " = ?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                return database.delete(BoaViagemContract.GASTO_PATH,
                        selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Uri desconhecida");
        }

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase database = helper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case VIAGEM_ID:
                selection = BoaViagemContract.Viagem._ID + " = ?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                return database.update(BoaViagemContract.VIAGEM_PATH, contentValues,
                        selection, selectionArgs);
            case GASTO_ID:
                selection = BoaViagemContract.Gasto._ID + " = ?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                return database.update(BoaViagemContract.GASTO_PATH, contentValues,
                        selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Uri desconhecida");
        }


    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case VIAGENS:
                return BoaViagemContract.Viagem.CONTENT_TYPE;
            case VIAGEM_ID:
                return BoaViagemContract.Viagem.CONTENT_ITEM_TYPE;
            case GASTOS:
            case GASTOS_VIAGEM_ID:
                return BoaViagemContract.Gasto.CONTENT_TYPE;
            case GASTO_ID:
                return BoaViagemContract.Gasto.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Uri desconhecida");
        }
    }


}

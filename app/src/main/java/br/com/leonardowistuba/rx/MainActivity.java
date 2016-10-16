package br.com.leonardowistuba.rx;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import br.com.leonardowistuba.rx.socket.ConnectionStatus;
import br.com.leonardowistuba.rx.socket.FabObservable;
import br.com.leonardowistuba.rx.socket.ObservableFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    private Subscription mSubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Observable.create(new FabObservable(fab)).subscribe(ObservableFactory.sharedInstance.emitClickAction);

        final TextView textViewConnectionStatus = (TextView) findViewById(R.id.connection_status);
        final TextView textViewUsersConnected = (TextView) findViewById(R.id.text_view_users_connected);
        final TextView textViewNumberOfClicks = (TextView) findViewById(R.id.text_view_number_of_clicks);

        mSubscribe = ObservableFactory.sharedInstance.connectionStatusObservable.map(new Func1<ConnectionStatus, String>() {
            @Override
            public String call(ConnectionStatus connectionStatus) {
                switch (connectionStatus.getStatus()) {
                    case Connected:
                        return "Conectado";
                    case ConnectionError:
                        return "Erro de conexão";
                    case Connecting:
                        return "Conectando...";
                    default:
                        break;
                }

                return null;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                textViewConnectionStatus.setText("Estado da conexão: " + s);
            }
        });

        ObservableFactory.sharedInstance.connectedUsersObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                textViewUsersConnected.setText("Usuários conectados: " + integer);
            }
        });

        ObservableFactory.sharedInstance.numberOfClicksObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                textViewNumberOfClicks.setText("Número de cliques: " + integer);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
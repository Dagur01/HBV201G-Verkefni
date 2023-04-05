package is.verkefni4.vidmot;

import is.verkefni4.vinnsla.Leikur;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Optional;

/******************************************************************************
 *  Nafn    : Ebba Þóra Hvannberg
 *  T-póstur: ebba@hi.is
 *
 *  Lýsing  : Controller klasinn fyrir leikinn - notandi getur fært bolta til
 *  vinstri eða hægri með örvatökkum.
 *****************************************************************************/
public class BouncingController {
    // fastar

    private static final int INTERVAL = 75;

    // Býr til beinan aðgang frá KeyCode og í heiltölu. Hægt að nota til að fletta upp
    // heiltölu fyrir KeyCode
    private static final HashMap<KeyCode, Stefna> map = new HashMap<KeyCode, Stefna>();

    // viðmóts tilviksbreytur
    @FXML
    private Label fxStig;
    @FXML
    private Leikbord fxLeikbord;

    private Timeline t; // tímalínan
    Leikur leikur; // vinnslan

    /**
     * Frumstilla controller eftir að búið er að hlaða inn .fxml skrá
     */
    public void initialize() {
        leikur = new Leikur();      // búa til vinnsluna
        fxStig.textProperty().bind(leikur.stiginProperty().asString()); // binda stigin við viðmótið
        fxStig.setFocusTraversable(false);    // ekki hægt að focus-a á stigin
    }

    /**
     * Tengir örvatakka við fall sem á að keyra í controller
     **/
    public void orvatakkar() {
        map.put(KeyCode.RIGHT, Stefna.HAEGRI);
        map.put(KeyCode.LEFT, Stefna.VINSTRI);
        // lambda fall - event er parameter
        fxStig.getScene().addEventFilter(KeyEvent.ANY,      //KeyEvents eru sendar á Scene
                this::adgerdLykill); // tilvísun í aðferðina (method reference) - kallað verður á aðferðina adgerdLykill
    }

    private void adgerdLykill(KeyEvent event) {
        try {
            if (map.get(event.getCode()) == null)
                event.consume(); // rangur lykill
            else
                fxLeikbord.getBolti().setStefna(map.get(event.getCode())); // flettum upp horninu fyrir KeyCode í map
        } catch (NullPointerException e) {
            event.consume();        // Ef rangur lykill er sleginn inn þá höldum við áfram
        }
    }

    /**
     * Stillir upp nýjum leik og byrjar hann
     */
    public void nyrLeikur() {
        leikur.nyrLeikur();
        fxLeikbord.nyrLeikur();
        t.play();
    }

    /**
     * Setur upp Animation fyrir leikinn og setur upp leikjalykkjuna
     */
    public void hefjaLeik() {
        KeyFrame k = new KeyFrame(Duration.millis(INTERVAL),
                e -> {
                    fxLeikbord.afram(); // boltinn færist áfram
                    fxLeikbord.aframPallar(); // pallar færast áfram
                    leikur.haekkaStigin(); // stigin eru hækkuð
                    if (fxLeikbord.erBoltiABotni()) { // athuga hvort boltinn hefur fallið á botninn
                        leikLokid("Boltinn fór útaf ");
                    }
                });
        t = new Timeline(k);    // búin til tímalína fyrir leikinn
        t.setCycleCount(Timeline.INDEFINITE);   // leikurinn leikur endalaust
        t.play();
    }

    /**
     * Leik er lokið eð skilaboðum til notanda sem segir ástæðuna.
     * Leikmaður spurður hvort hann vilji leika annan leik
     *
     * @param skilabod ástæðan fyrir að leik lauk
     */
    public void leikLokid(String skilabod) {
        t.stop();
        Platform.runLater(() -> synaAlert(skilabod));
    }

    /**
     * Spyr notanda hvort hann vilji leika annan leik. Hefur nýjan leik ef svo er
     *
     * @param s skilaboð
     */
    private void synaAlert(String s) {
        Alert a = new AdvorunDialog("", "Bouncing Ball", s + " Viltu leika annan leik? ");
        Optional<ButtonType> u = a.showAndWait();
        if (u.isPresent() && !u.get().getButtonData().isCancelButton())
            nyrLeikur();
        else
            System.exit(0);
    }



    // test forrit
    public void testBolti() {
        fxLeikbord.getBolti().setStefna(Stefna.NIDUR);
        for (int i = 0; i < 50; i++) {
            fxLeikbord.getBolti().afram();
        }
    }
}
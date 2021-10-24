package ax.stardust.skvirrel.component.dialog;

/**
 * Interface for dialog interactions.
 */
public interface DialogInteractionListener {

    /**
     * Action for when a positive(ok/yes etc.) button has been pressed within a dialog
     */
    void onPositiveButtonPressed();

    /**
     * Action for when a negative(no/cancel etc.) button has been pressed within a dialog
     */
    void onNegativeButtonPressed();

    /**
     * Action for when a neutral(close etc.) button has been pressed within a dialog
     */
    void onNeutralButtonPressed();
}

package sk.stuba.fei.uim.oop;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel {
    @Getter @Setter private JLabel gamesWon = (new JLabel("Games Won"));

    public ControlPanel(ActionListener actionListener) {
        super();
        setLayout(new GridLayout(2,3));

        JButton[] buttons = new JButton[5];
        buttons[0] = (new JButton("UP"   ));
        buttons[1] = (new JButton("RESET"));
        buttons[2] = (new JButton("LEFT" ));
        buttons[3] = (new JButton("DOWN" ));
        buttons[4] = (new JButton("RIGHT"));

        this.add(gamesWon);

        for (var butt: buttons){
            butt.setFocusable(false);

            butt.setActionCommand(butt.getText());
            butt.addActionListener(actionListener);
            this.add(butt);
        }

    }
}

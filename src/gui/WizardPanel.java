package gui;

import javax.swing.JPanel;

public abstract class WizardPanel extends JPanel
{
	public abstract Messages canProceed();

	public abstract void proceed();

	public abstract String getTitle();

	public abstract String getDescription();

}
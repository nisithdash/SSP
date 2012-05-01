Ext.define('Ssp.store.Tools', {
    extend: 'Ext.data.Store',
    model: 'Ssp.model.Tool',
	autoLoad: false,
    data: [{ name: "Profile", toolType: "Profile" },
           { name: "Student Intake", toolType: "StudentIntake" },
           { name: "Action Plan", toolType: "ActionPlan" }]	
});
var mac = "8 0 20 c0 ff ee";
var hostID = "c0ffee"
var delay = 22;

var WshShell = WScript.CreateObject("WScript.Shell");

confirm = WshShell.Popup("Press OK to bring PuTTY into foreground and fix NVRAM", 120, "Fix NVRAM", 1);
if (confirm != 1)
	WScript.Quit(666);

WshShell.AppActivate("COM1 - PuTTY");
WScript.Sleep(100);

sendKeys("17 0 mkp");
sendKeys(mac + " " + hostID + " mkpl");
WshShell.SendKeys("^D");
WScript.Sleep(2*delay);
WshShell.SendKeys("^R");

sendKeys("set-defaults");
WScript.Sleep(2500);

sendKeys("setenv diag-switch? false");
sendKeys("setenv auto-boot? false");

sendKeys("reset");

WScript.Echo("finished :-)");


function sendKeys(keys)
{
	for (var i=0; i<keys.length; i++) {
		WScript.Sleep(delay);
		WshShell.SendKeys(keys.charAt(i));
	}
	WshShell.SendKeys("{ENTER}");
}

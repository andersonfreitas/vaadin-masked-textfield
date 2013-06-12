# MaskedTextField for Vaadin

This component is listed on [Vaadin Library](http://vaadin.com/addon/maskedtextfield)

It is fully compatible with the OSGI architecture of Vaadin 7





Overview of currently available masks:

# - any digit
U - upper-case letter
L - lower-case letter
? - any letter
A - any number or character
* - anything
H - hex sign (0-9, a-f or A-F)
' - Escape character, used to escape any of the special formatting characters.
~ - +/- sign

Any character not matching one of the above mask character or if it escaped with the single quote character (') is considered to be a literal.

Some mask examples:

Phone Number: (###) ###-####
USPS Express Mail: EU#########'US
Date / time: ##/##/#### ##:##
State: UU
HTML Color: '#HHHHHH
An capitalized 6 letter word: ULLLLL



TODO
=========================================
- setValidChars(",. ")
- Create an tab "try it" to allow the Demo App user to specify the mask and see the mask applied in real-time.
- Deploy demo app somewhere



Known issues:
=========================================
None. =)



FUTURE ROADMAP
=========================================
 - Support for left aligned input with 0 filling.
 - Currency and thousand separators


CHANGELOG
=========================================
* 30th June 2011 (tag 0.1.2)
- tab key press corrected
- cursor is positioned in first position allowed when field is focused.

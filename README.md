# CryptoSmite
Unenrollment exploit that uses stateful files to unenroll for kernver <= 3. Use [badrecovery](https://github.com/BinBashBanana/badrecovery) if you have kernver 4.
## How does it work?
We use stateful "backups" that basically allows us to change the encrypted contents of the stateful partition, to arbritary contents. This data is useful for enrollment status, so we changed it to make the device appear unenrolled. On the OOBE, it starts the AutoEnrollmentController, which chains into the ash ownership system, and then the ownership system checks for a file. If this file exists, it removes FWMP. 

## Usage instructions
To use this, you need to look at the instructons [here](https://docs.google.com/presentation/d/1MciRMbDEb3RJomH2gYW9C5qRVjS4P92o2s4QepoCSgY/edit#slide=id.p).

## Any further questions?
Please ask questions in the support server. <b> @unretained is back. please ask @unretained on discord for any support.</b>

### WE AREN'T LIABLE NOR RESPONSIBLE FOR ANY DAMAGE/ISSUES CAUSED BY THIS EXPLOIT! DO NOT CONTACT US FOR ANY ISSUES CAUSED BY THIS EXPLOIT!

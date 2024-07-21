Chris Yocum     July 20, 2024

see below Self-Assessment
verify QEMU operational and AWS site can be reached prior to testing

Software Versions:
  busybox-1.36.1
  host-qemu-8.1.1
  libcurl-8.7.1openssh-9.1p1
    (shows 7.81.0 upon curl --version Ubuntu VM base OS)
  openssh
  python3 3.10.12 (Ubuntu VM base OS)
  python3 3.13.3 (server)
  apache maven 3.9.8 (server)
  MySQL mysql  Ver 8.0.37-0ubuntu0.24.04.1 for Linux on x86_64 ((Ubuntu)) 
  Vagrant 2.4.1
  

Grading Criteria:
  110%: All the below and you use HTTPS to communicate with the cloud!
  100%: Everything works as described in the project presentations in module 3.
  80%: The program runs, but doesn't pay attention to the status file, or doesn't handle programs correctly.
  60%: The program runs, but doesn't start up on boot.
  0%: Your client device doesn't boot.

Self-Assessment:
  A. Client device does boot, 
  B. program does run, 
  C. boot
    a. does run upon buildroot login and QEMU launch (./tcsimd not needed if S80 file placed properly),
       justification - upon factory software installation, software will auto boot upon user power on via USB adapter plugin
  D. handles temp correctly to server
  E. Status transmits, toggles upon max or min temp reached
  F. Morning, noon and night min/max temps in settings table
  

Installation Guidance:
  Initiating QEMU:
    A.	Performing Server set up may be desired first for webpage and clear table.
    B.	Maneuver to the buildroot file
    C.	Initiate QEMU shell via 		./qemu_run_versatile.sh
        a.	Log in using username: 	iottemp
        b.	Using password:		zxcv
        c.	Password is local only for device emulation and not part of the distro upon release!!
    D.	Perform listing				ls
    E.	Observe if file is present ->  tcsimd
        a.	Delete as necessary		rm tcsimd
    F.	Switch to super user			su –
    G.	Maneuver to /usr/bin			cd /usr/sbin
        a.	Delete tcsimd if present
    H.	Open new Terminal and use Github	
        a.	For Github transfer		scp -P 2222 ./tcsimd iottemp@127.0.0.1:~/
        b.	Also transfer daemon		scp -P 2222 ./S80tcsimd iottemp@127.0.0.1:~/
    I.	Go back to Terminal in QEMU
    J.	As SU, go back to home		cd /home
    K.	Transfer tcsimd to sbin			mv tcsimd /usr/sbin
    L.	Transfer daemon to init		mv S80tcsimd /etc/init.d
    O.	Reboot QEMU for daemon to load
        a.	Else start tcsmid manually	./tcsimd
        b.  Verify killall tcsimd if wanting to stop incrementation

Initiating the Server: 
    A.	Connect to the server			
        ssh -i "iottemp1aws.pem" ubuntu@ec2-3-139-156-162.us-east-2.compute.amazonaws.com
    B.	Maneuver to MySQL, database and table to delete rows as necessary
        a.	Root password to enter:	Coyote1985%$
        b.	Use finaltemp and temp
        c.	Clear table rows		DELETE FROM temp;
    C.	On directory, maneuver to /ubuntu-test/proj/ChrisServer
    D.	Execute compile shell			./compiling.sh
        a.	Shell gives IP to table temp	http://3.139.156.162:8080/temp 
    E.	Opening browser to IP			
        a.	Should yield “Tempertaure get request is empty.”
        b.	Else, database finaltemp and table temp is not cleared.
        c.	If a., then waiting to start processing on QEMU.
        d.	--- Clearing table does NOT reset id counter. ---


#!/bin/sh
# script is used to access QEMU for ARM
#=======================================================================================================
qemu-system-arm -M versatilepb -m 256 -kernel output/images/zImage -dtb output/images/versatile-pb.dtb -drive file=output/images/rootfs.ext2,if=scsi,format=raw -append "root=/dev/sda console=ttyAMA0,115200" -serial stdio -net nic,model=rtl8139 -net user,hostfwd=tcp::2222-:22

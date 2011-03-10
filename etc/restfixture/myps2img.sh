#!/bin/sh

#/////////////// Description ////////////////////////////
#
#pstoimg : convert *.ps file to *.gif or *.png
#
# input  : first parameter
# output : stdout
#
# reference : http://www.dais.is.tohoku.ac.jp/~kabe/misc/tips.html
#
# requires : ghostscript (gs)
#            ppm file support (pnmdepth, ppmtogif)
#

#////////////////Update Logs/////////////////////////////
#
#      ver.0.01 by AGATASHI  20000426
#              :original version
#            02              ditto   
#              :pnmdepth -> ppmquant
#            03              ditto
#              :gs option changed. -sDEVICE=ppm -> -sDEVICE=ppmraw
#
#          0.10 by AGATASHi  20010516
#              :--transparent option
#            11 by AGATASHI  20010606
#              : --dpi option
#            12 by AGATASHI  20010607
#              : --rotate option
#          0.20 by AGATASHI  20010607
#              : All options now not need "="
#            21 by AGATASHI  20010618
#              : Minor bug fixes
#            22 by AGATASHI  20010623
#              : Change of usage system
#              : Dynamic specification of size of working field
#              : --margin option#
#            23 by AGATASHI  20010624
#              : --detail_ratio option
#            24 by AGATASHI 20010624
#              : --usage and --help option
#
#
#          1.01 by AGATASHI 20020911
#              : merge myps2png.sh and myps2gif.sh
#              : now supports both GIF and PNG
#              : new file name = myps2img
#
#
#          1.02 by AGATASHI 20030107
#             : small correction on usage message
#             : CONVERT_COMMAND is now automatically set.
#
#
#
#///////////////  Version Number /////////////////////////////
# 
VERSION_MAJOR="1"
VERSION_MINOR="02"
#


#//////////////// Default Values /////////////////////////////
#

#DETAIL_DEFAULT=2.0  
DETAIL_DEFAULT=1.0
MARGIN_DEFAULT=10
DPI_DEFAULT=100
ROTATE_DEFAULT=0
X_SIZE_INCH_DEFAULT=12
Y_SIZE_INCH_DEFAULT=12
TEXT_ALPHA_BITS_DEFAULT=4
GRAPHICS_ALPHA_BITS_DEFAULT=4

#//////////////// Usage /////////////////////////////
#

usage(){

cat <<EOM

Usage:
$COMNAME [options...] ps_file image_file

Description:
$COMNAME converts a *.ps file into one GIF or PNG image file.
Current Version is ${VERSION_MAJOR}.${VERSION_MINOR}

Options : 
--detail_ratio=n        [default = ${DETAIL_DEFAULT}]
--dpi pix_per_inch=n    [default = ${DPI_DEFAULT}]
--margin_width=n        [default = ${MARGIN_DEFAULT}]
--margin=n               (ditto)
--rotate=n              [default = ${ROTATE_DEFAULT} (counterclockwise in deg)]
--transparent=colorname [default no transparent color set]
--graphics_alpha_bits=n [default = ${GRAPHICS_ALPHA_BITS_DEFAULT}]
--text_alpha_bits=n     [default = ${TEXT_ALPHA_BITS_DEFAULT}]

--usage | --help : show this message

EOM
}

#
#//////////////// Main Body /////////////////////////////
#

COMNAME=`basename $0`

if [ $# -lt 2 ] ; then
	usage
	exit 1
fi

#////////////////////////////////////////////
#
# Command Line Options
#
#

TRANSPARENT_OPTION=""
DPI=$DPI_DEFAULT
DETAIL_RATIO=$DETAIL_DEFAULT
MARGIN_WIDTH=$MARGIN_DEFAULT
ROTATE_ANGLE=$ROTATE_DEFAULT
X_SIZE_INCH=$X_SIZE_INCH_DEFAULT
Y_SIZE_INCH=$Y_SIZE_INCH_DEFAULT
TEXT_ALPHA_BITS=$TEXT_ALPHA_BITS_DEFAULT
GRAPHICS_ALPHA_BITS=$GRAPHICS_ALPHA_BITS_DEFAULT

  while [ $# -gt 0 ] ; do
    #echo "Q$2" | grep '^Q--' > /dev/null 
    #test $? -ne 0 -a $# -gt 1           # process this option?

    KEY=`echo $1 | sed -n 's/--\([a-zA-Z0-9_]*\).*/\1/p'`
    VALUE=`echo $1 | sed -n 's/--[a-zA-Z0-9_]*=\(.*\)/\1/p'`

    if [ -z "$KEY" ] ; then
	break
    fi

    if [ -n "$KEY" -a -z "$VALUE" ] ; then
        if [ "$KEY" != "usage" -a "$KEY" != "help" ] ; then
		cat<<END
Error: Illegal option syntax : $1
END
		exit
	fi
    fi

    case "$KEY" in

      help|usage)
        usage; exit
        ;;

      transparent)
	TRANSPARENT_OPTION=$VALUE
        ;;
   
      dpi)
	DPI=$VALUE
        ;;
   
      rotate)
	ROTATE_ANGLE=$VALUE
        ;;
          
      margin_width|margin)
	MARGIN_WIDTH=$VALUE
        ;;
          
      detail_ratio)
	DETAIL_RATIO=$VALUE
        ;;
     
      graphics_alpha_bits)
	GRAPHICS_ALPHA_BITS=$VALUE
	;;
     
      text_alpha_bits)
	TEXT_ALPHA_BITS=$VALUE
	;;
     
       *)
	echo "Unrecognized option : $1"
	usage
	exit
	;;

    esac

    shift
  done

  if [ $# -ne 2 ] ; then
	usage
	exit
  fi


#////////////////////////////////////////////
#
# GO
#
#

if [ -f "$1" ] ; then

	  #file names

	PS_FILE=$1
	RESULT_FILE=$2

	  #details (working field dpi setting)

	REDUCTION_RATIO=`echo $DETAIL_RATIO | awk '{printf("%.3f", 1 / $1) }'`

	  #dpi of working field

	WORK_DPI=`echo $DETAIL_RATIO $DPI | awk '{printf("%d", $1 * $2) }'`

	  #size of working field in pixels

	PIX_X_SIZE=`echo $WORK_DPI $X_SIZE_INCH | awk '{printf("%d", $1 * $2) }'`
	PIX_Y_SIZE=`echo $WORK_DPI $Y_SIZE_INCH | awk '{printf("%d", $1 * $2) }'`

    # Execute...

	echo "----------- $COMNAME version ${VERSION_MAJOR}.${VERSION_MINOR} -----------"
	echo "  Source : $PS_FILE ,  Target : $RESULT_FILE "

	rm -f $RESULT_FILE

	if [ $ROTATE_ANGLE -ne 0 ] ; then
		ROTATE_COMMAND="pnmrotate $ROTATE_ANGLE"
	else
		ROTATE_COMMAND="cat"
	fi

	#
	#
	#

	if [ $MARGIN_WIDTH -ne 0 ] ; then
		MARGIN_COMMAND="pnmmargin -white $MARGIN_WIDTH"
	else
		MARGIN_COMMAND="cat"
	fi

	#
	#
	#

	if [ $DETAIL_RATIO = "1.0" ] ; then
		SCALE_COMMAND='cat'
	else
		SCALE_COMMAND="pnmscale $REDUCTION_RATIO"

	fi

	#
	#
	#

	#COLOR_REDUCTION_COMMAND='ppmquant 256'
	#COLOR_REDUCTION_COMMAND="ppmdither -blue 10 -red 10 -green 10"
	COLOR_REDUCTION_COMMAND='pnmdepth 15'


	#
	# netpbm files/dir
	#

	case $RESULT_FILE in
		*.gif)
		  CONVERT_COMMAND=`which ppmtogif`
		  ;;

		*.png)
		  CONVERT_COMMAND=`which pnmtopng`
		  ;;

	        *)
		  echo 'Error : Sorry now the target must be either *.gif or *.png'
		  exit
		  ;;
	esac

	if [ -z "$CONVERT_COMMAND" ] ; then
		cat <<EOM
Fatal Error : $COMNAME : Path to netpbm convert command is not set
EOM
		exit
	fi

	#
	# ps --> pnm(ppm) --> png
	# TODO: size information will be got from PS file itself
	#

	gs -sDEVICE=ppmraw \
            -dTextAlphaBits=${TEXT_ALPHA_BITS} \
	    -dGraphicsAlphaBits=${GRAPHICS_ALPHA_BITS} \
	    -sOutputFile=- \
	    -g${PIX_X_SIZE}x${PIX_Y_SIZE} \
	    -r${WORK_DPI} \
	    -q -dNOPAUSE ${PS_FILE}  < /dev/null  |
	  pnmcrop -white    | \
	  ${ROTATE_COMMAND} | \
	  ${SCALE_COMMAND}  | \
	  ${MARGIN_COMMAND} | \
	  ${COLOR_REDUCTION_COMMAND}      | ppmquant 256 | \
	  ${CONVERT_COMMAND} -interlace $TRANSPARENT_OPTION - \
	  > $RESULT_FILE

else
	echo Fatal Error : $COMNAME : FILE \"$1\" Not Found
	exit 1
fi

#
# EOF
#


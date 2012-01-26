#!/bin/sh
export PATH=$PATH:/usr/local/bin

set -e

SOURCE_DIR=$1
BUILD_DIR=$2
RELEASE_DIR=$3

if [ ! -d "$SOURCE_DIR" ] | [ ! -d "$BUILD_DIR" ] | [ ! -d "$RELEASE_DIR" ]  
 then
  echo "Usage: create_release_packages.sh <sourcedir> <builddir> <releasedir>" 
  echo "given paths must exist"  
exit
fi

###################### DOCUMENTATION #####################
#TODO: fix this, temporary solution
mv $SOURCE_DIR/target/site $RELEASE_DIR/documentation
rm -rf $SOURCE_DIR/target


###################### CORE_PACKAGE #####################
CORE_RELEASE_BUILD_DIR=$BUILD_DIR/core-release
rm -rf $CORE_RELEASE_BUILD_DIR
mkdir $CORE_RELEASE_BUILD_DIR

cp $SOURCE_DIR/LICENSE.txt $CORE_RELEASE_BUILD_DIR
cp -r $RELEASE_DIR/documentation $CORE_RELEASE_BUILD_DIR
cp $RELEASE_DIR/dtangler-core.jar $CORE_RELEASE_BUILD_DIR

cd $CORE_RELEASE_BUILD_DIR
zip -r dtangler-core.zip *
cd -
cp $CORE_RELEASE_BUILD_DIR/dtangler-core.zip $RELEASE_DIR

###################### SWINGUI PACKAGE #####################
SWINGUI_RELEASE_BUILD_DIR=$BUILD_DIR/swingui-release
rm -rf $SWINGUI_RELEASE_BUILD_DIR
mkdir $SWINGUI_RELEASE_BUILD_DIR

cp $SOURCE_DIR/LICENSE.txt $SWINGUI_RELEASE_BUILD_DIR
cp -r $RELEASE_DIR/documentation $SWINGUI_RELEASE_BUILD_DIR

cp $SOURCE_DIR/dtangler-swingui/dsmgui.bat $SWINGUI_RELEASE_BUILD_DIR
cp $SOURCE_DIR/dtangler-swingui/dsmgui.sh $SWINGUI_RELEASE_BUILD_DIR

mkdir $SWINGUI_RELEASE_BUILD_DIR/lib-forms
cp $SOURCE_DIR/lib-forms/forms-1.2.0.jar $SWINGUI_RELEASE_BUILD_DIR/lib-forms
cp $SOURCE_DIR/lib-forms/LICENSE.txt $SWINGUI_RELEASE_BUILD_DIR/lib-forms

cp $RELEASE_DIR/dtangler-gui.jar $SWINGUI_RELEASE_BUILD_DIR

cd $SWINGUI_RELEASE_BUILD_DIR
zip -r dtangler-gui.zip *
cd -
cp $SWINGUI_RELEASE_BUILD_DIR/dtangler-gui.zip $RELEASE_DIR


###################### SRC_PACKAGE #####################
zip -r $RELEASE_DIR/dtangler-src.zip $SOURCE_DIR/* -x@src-exclude.lst



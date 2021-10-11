from setuptools import setup

setup(name='frogspawn',
      version='1.3.2',
      description='Frogspawn - A Fast Recursive Spectral Graph Partitioner',
      url='https://github.com/florianschafer/frogspawn',
      author='Florian Schaefer',
      author_email='florians@mailbox.org',
      license='Apache License 2.0',
      include_package_data=True,
      packages=['frogspawn'],
      package_data={'frogspawn': ['jars/*.jar']},
      install_requires=[
          'jpype1>=1.3.0',
      ],
      zip_safe=False)
